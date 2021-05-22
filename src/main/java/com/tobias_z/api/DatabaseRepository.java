package com.tobias_z.api;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import com.tobias_z.DBConfig;
import com.tobias_z.DBSetting;
import com.tobias_z.Database;
import com.tobias_z.ExecutedQuery;
import com.tobias_z.SQLQuery;
import com.tobias_z.annotations.AutoIncremented;
import com.tobias_z.annotations.Column;
import com.tobias_z.annotations.PrimaryKey;
import com.tobias_z.annotations.Table;
import com.tobias_z.api.insert.Insert;
import com.tobias_z.exceptions.DatabaseException;
import com.tobias_z.exceptions.NoGeneratedKeyFound;
import com.tobias_z.exceptions.NoPrimaryKeyFound;
import com.tobias_z.exceptions.NoTableFound;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabaseRepository implements Database {

    private final DBConfig config;
    private final Utils utils;

    public DatabaseRepository(DBConfig config) {
        this.config = config;
        this.utils = new Utils();
        try {
            Class.forName(config.getConfiguration().get(DBSetting.JDBC_DRIVER));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("jdbc-driver not found: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        Map<DBSetting, String> dbConfiguration = config.getConfiguration();
        String user = dbConfiguration.get(DBSetting.USER);
        String password = dbConfiguration.getOrDefault(DBSetting.PASSWORD, null);
        String url = dbConfiguration.get(DBSetting.URL);
        return DriverManager.getConnection(url, user, password);
    }

    private void generateFullSQLStatement(SQLQuery query) {
        query.getParameters()
            .forEach((name, value) -> {
                String valueToUse = value;
                try {
                    Integer.parseInt(valueToUse);
                } catch (NumberFormatException e) {
                    valueToUse = "'" + valueToUse + "'";
                }
                query.setSql(query.getSql().replace(":" + name, valueToUse));
            });
    }

    @Override
    public <T> ExecutedQuery<T> insert(SQLQuery query, Class<T> dbTableClass)
        throws DatabaseException, NoGeneratedKeyFound, NoPrimaryKeyFound {
        try (Connection connection = getConnection()) {
            generateFullSQLStatement(query);
            Pair<String, Object> keyAndValue;
            Insert insert = new Insert(connection, query.getSql());
            if (utils.isWithGeneratedKey(dbTableClass)) {
                keyAndValue = insert.withGeneratedKey(dbTableClass);
            } else {
                insert.withoutGeneratedKey();
                keyAndValue = utils.getPrimaryKeyAndValue(dbTableClass, query);
            }
            return () -> {
                Table table = utils.getTableAnnotation(dbTableClass);
                String fieldName = keyAndValue.left;
                Object value = keyAndValue.right;

                try (Connection conn = getConnection()) {
                    var ps = conn.prepareStatement(
                        "SELECT * FROM " + table.name() + " WHERE " + fieldName + " = " + value);
                    ResultSet resultSet = ps.executeQuery();
                    ResultSetMapper<T> mapper = new ResultSetMapper<>();
                    return mapper.mapSingleResult(dbTableClass, resultSet);
                } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                    throw new DatabaseException(e.getMessage());
                }
            };
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void insert(SQLQuery query) throws DatabaseException {
        try (Connection connection = getConnection()) {
            generateFullSQLStatement(query);
            Insert repository = new Insert(connection, query.getSql());
            repository.withoutGeneratedKey();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public <T, PrimaryKey> T get(PrimaryKey primaryKey, Class<T> dbTableClass)
        throws DatabaseException, NoTableFound, NoGeneratedKeyFound {
        try (Connection connection = getConnection()) {
            Table table = utils.getTableAnnotation(dbTableClass);
            Column column = utils.getPrimaryKeyColumn(dbTableClass);
            PrimaryKey foundPrimaryKey = utils.getPrimaryKeyStringForSQL(primaryKey);
            var ps = connection.prepareStatement(
                "SELECT * FROM " + table.name() + " WHERE " + column.name() + " = " + foundPrimaryKey
            );
            ResultSet resultSet = ps.executeQuery();
            ResultSetMapper<T> mapper = new ResultSetMapper<>();
            return mapper.mapSingleResult(dbTableClass, resultSet);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public <T> List<T> select(SQLQuery query, Class<T> dbTableClass) throws DatabaseException {
        try (Connection connection = getConnection()) {
            generateFullSQLStatement(query);
            var ps = connection.prepareStatement(query.getSql());
            ResultSet resultSet = ps.executeQuery();
            ResultSetMapper<T> mapper = new ResultSetMapper<>();
            return mapper.mapListOfResults(dbTableClass, resultSet);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public <T> List<T> getAll(Class<T> dbTableClass) throws DatabaseException, NoTableFound {
        try (Connection connection = getConnection()) {
            Table table = utils.getTableAnnotation(dbTableClass);
            var ps = connection.prepareStatement("SELECT * FROM " + table.name());
            ResultSet resultSet = ps.executeQuery();
            ResultSetMapper<T> mapper = new ResultSetMapper<>();
            return mapper.mapListOfResults(dbTableClass, resultSet);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}