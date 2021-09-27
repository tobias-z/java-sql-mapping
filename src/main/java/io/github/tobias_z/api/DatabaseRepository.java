package io.github.tobias_z.api;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.DBSetting;
import io.github.tobias_z.DBStatement;
import io.github.tobias_z.Database;
import io.github.tobias_z.annotations.Column;
import io.github.tobias_z.annotations.Table;
import io.github.tobias_z.exceptions.DatabaseException;
import io.github.tobias_z.exceptions.NoGeneratedKeyFound;
import io.github.tobias_z.exceptions.NoPrimaryKeyFound;
import io.github.tobias_z.exceptions.NoTableFound;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

class DatabaseRepository implements Database {

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

    @Override
    public <T> T insert(DBStatement statement, Class<T> dbTableClass)
            throws DatabaseException, NoGeneratedKeyFound, NoPrimaryKeyFound, NoTableFound {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = statement.getPreparedStatement(connection)
        ) {
            if (utils.isWithGeneratedKey(dbTableClass)) {
                return new Insert(preparedStatement, utils).withGeneratedKey(dbTableClass);
            }
            throw new DatabaseException("Insert was called to return a class. But no Auto increment annotation was found");
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public <T, PrimaryKey> T get(PrimaryKey primaryKey, Class<T> dbTableClass)
            throws DatabaseException, NoTableFound {
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
    public <T> List<T> select(DBStatement statement, Class<T> dbTableClass) throws DatabaseException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = statement.getPreparedStatement(connection)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
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
    public void executeQuery(DBStatement statement) throws DatabaseException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = statement.getPreparedStatement(connection)
        ) {
            preparedStatement.execute();
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