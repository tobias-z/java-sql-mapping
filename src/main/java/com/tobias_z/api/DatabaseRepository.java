package com.tobias_z.api;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import com.tobias_z.DBConfig;
import com.tobias_z.DBSetting;
import com.tobias_z.Database;
import com.tobias_z.SQLQuery;
import com.tobias_z.annotations.Column;
import com.tobias_z.annotations.Table;
import com.tobias_z.exceptions.DatabaseException;
import com.tobias_z.exceptions.NoGeneratedKeyFound;
import com.tobias_z.exceptions.NoPrimaryKeyFound;
import com.tobias_z.exceptions.NoTableFound;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    private SQLQuery getSavedQuery(SQLQuery query) {
        SQLQuery savedQuery = new SQLQuery(query.getSql());
        query.getParameters().forEach(savedQuery::addParameter);
        return savedQuery;
    }

    private <T> T getByPrimaryKey(Class<T> dbTableClass, Pair<String, Object> keyAndValue,
        Connection connection)
        throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Table table = utils.getTableAnnotation(dbTableClass);
        String fieldName = keyAndValue.left;
        Object value = keyAndValue.right;

        PreparedStatement ps = connection.prepareStatement(
            "SELECT * FROM " + table.name() + " WHERE " + fieldName + " = " + value);
        ResultSet resultSet = ps.executeQuery();
        ResultSetMapper<T> mapper = new ResultSetMapper<>();
        return mapper.mapSingleResult(dbTableClass, resultSet);
    }

    @Override
    public <T> T insert(SQLQuery query, Class<T> dbTableClass)
        throws DatabaseException, NoGeneratedKeyFound, NoPrimaryKeyFound, NoTableFound {
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
            return getByPrimaryKey(dbTableClass, keyAndValue, connection);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void insert(SQLQuery query) throws DatabaseException {
        try (Connection connection = getConnection()) {
            generateFullSQLStatement(query);
            Insert insert = new Insert(connection, query.getSql());
            insert.withoutGeneratedKey();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void update(SQLQuery query) throws DatabaseException {
        try (Connection connection = getConnection()) {
            generateFullSQLStatement(query);
            PreparedStatement statement = connection.prepareStatement(query.getSql());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public <T> T update(SQLQuery query, Class<T> dbTableClass)
        throws DatabaseException {
        try (Connection connection = getConnection()) {
            SQLQuery savedQuery = getSavedQuery(query);
            generateFullSQLStatement(query);
            PreparedStatement statement = connection.prepareStatement(query.getSql());
            statement.executeUpdate();
            Pair<String, Object> keyAndValue = utils.getPrimaryKeyAndValue(dbTableClass, query);
            keyAndValue = utils.updateValueIfSettingPrimayKey(dbTableClass, savedQuery, query, keyAndValue);
            return getByPrimaryKey(dbTableClass, keyAndValue, connection);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
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
            PreparedStatement ps = connection.prepareStatement(
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
            PreparedStatement ps = connection.prepareStatement(query.getSql());
            ResultSet resultSet = ps.executeQuery();
            ResultSetMapper<T> mapper = new ResultSetMapper<>();
            return mapper.mapListOfResults(dbTableClass, resultSet);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public <T, PrimaryKey> void delete(PrimaryKey primaryKey, Class<T> dbTableClass)
        throws DatabaseException {
        try (Connection connection = getConnection()) {
            Table table = utils.getTableAnnotation(dbTableClass);
            Column column = utils.getPrimaryKeyColumn(dbTableClass);
            PrimaryKey foundPrimaryKey = utils.getPrimaryKeyStringForSQL(primaryKey);

            PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM " + table.name() + " WHERE " + column.name() + " = " + foundPrimaryKey
            );
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void delete(SQLQuery query) throws DatabaseException {
        try (Connection connection = getConnection()) {
            generateFullSQLStatement(query);
            PreparedStatement statement = connection.prepareStatement(query.getSql());
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public <T> List<T> getAll(Class<T> dbTableClass) throws DatabaseException, NoTableFound {
        try (Connection connection = getConnection()) {
            Table table = utils.getTableAnnotation(dbTableClass);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table.name());
            ResultSet resultSet = ps.executeQuery();
            ResultSetMapper<T> mapper = new ResultSetMapper<>();
            return mapper.mapListOfResults(dbTableClass, resultSet);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}