package com.tobias_z.api;

import com.tobias_z.DBConfig;
import com.tobias_z.DBConnection;
import com.tobias_z.Database;
import com.tobias_z.Inserted;
import com.tobias_z.SQLQuery;
import com.tobias_z.annotations.Column;
import com.tobias_z.annotations.PrimaryKey;
import com.tobias_z.annotations.Table;
import com.tobias_z.api.insert.Insert;
import com.tobias_z.domain.User;
import com.tobias_z.exceptions.DatabaseException;
import com.tobias_z.exceptions.NoGeneratedKeyFound;
import com.tobias_z.exceptions.NoTableFound;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DatabaseRepository implements Database {

    private final DBConfig config;

    public DatabaseRepository(DBConfig config) {
        this.config = config;
        try {
            Class.forName(config.getConfiguration().get("jdbc-driver"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("jdbc-driver not found: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        Map<String, String> dbConfiguration = config.getConfiguration();
        String user = dbConfiguration.get("user");
        String password = dbConfiguration.getOrDefault("password", null);
        String url = dbConfiguration.get("url");
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

    private <T> Table getTableAnnotation(Class<T> dbTableClass) throws NoTableFound {
        Table table = dbTableClass.getAnnotation(Table.class);
        if (table == null) {
            throw new NoTableFound("Did not find a Table name for: " + dbTableClass.getName());
        }
        return table;
    }

    private <T> Column getPrimaryKeyColumn(Class<T> dbTableClass) throws NoGeneratedKeyFound {
        Field[] fields = dbTableClass.getDeclaredFields();
        for (Field field : fields) {
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            Column column = field.getAnnotation(Column.class);
            if (primaryKey != null && column != null) {
                return column;
            }
        }
        throw new NoGeneratedKeyFound("No PrimaryKey annotation was found on: " + dbTableClass.getName());
    }

    @Override
    public <T> Inserted<T> insert(SQLQuery query, Class<T> dbTableClass)
        throws DatabaseException, NoGeneratedKeyFound {
        try (Connection connection = getConnection()) {
            generateFullSQLStatement(query);
            Insert insert = new Insert(connection, query.getSql());
            LinkedHashMap<String, Integer> keyAndValues = insert.withGeneratedKey(dbTableClass);
            return () -> {
                Table table = getTableAnnotation(dbTableClass);
                Entry<String, Integer> keyAndValue = keyAndValues.entrySet().iterator().next();
                int id = keyAndValue.getValue();
                String fieldName = keyAndValue.getKey();

                try (Connection conn = getConnection()) {
                    var ps = conn.prepareStatement(
                        "SELECT * FROM " + table.name() + " WHERE " + fieldName + " = ?;");
                    ps.setInt(1, id);
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
            Table table = getTableAnnotation(dbTableClass);
            Column column = getPrimaryKeyColumn(dbTableClass);
            var ps = connection.prepareStatement(
                "SELECT * FROM " + table.name() + " WHERE " + column.name() + " = " + primaryKey
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
            Table table = getTableAnnotation(dbTableClass);
            var ps = connection.prepareStatement("SELECT * FROM " + table.name());
            ResultSet resultSet = ps.executeQuery();
            ResultSetMapper<T> mapper = new ResultSetMapper<>();
            return mapper.mapListOfResults(dbTableClass, resultSet);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Database db = DBConnection.createConnection(new Config());
        try {
            List<User> users = db.getAll(User.class);

            User user2 = db.get(1, User.class);

            SQLQuery allQuery = new SQLQuery("SELECT * FROM users");
            List<User> users2 = db.select(allQuery, User.class);

            SQLQuery insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
                .addParameter("name", "Bob Martin Jjjj");
            User user = db.insert(
                insertUserQuery,
                User.class
            ).getGeneratedEntity();
        } catch (DatabaseException | NoTableFound | NoGeneratedKeyFound e) {
            e.printStackTrace();
        }
    }
}