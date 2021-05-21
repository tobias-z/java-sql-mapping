package com.tobias_z.api.insert;

import com.tobias_z.api.ResultSetMapper;
import com.tobias_z.exceptions.NoGeneratedKeyFound;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class Insert {

    private final Connection connection;
    private final String query;

    public Insert(Connection connection, String query) {
        this.connection = connection;
        this.query = query;
    }

    public void withoutGeneratedKey() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        statement.executeUpdate();
    }

    public <T> LinkedHashMap<String, Integer> withGeneratedKey(Class<T> dbTableClass)
        throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoGeneratedKeyFound {
        PreparedStatement statement = connection.prepareStatement(
            query,
            Statement.RETURN_GENERATED_KEYS
        );
        statement.executeUpdate();
        ResultSet resultSet = statement.getGeneratedKeys();
        ResultSetMapper<T> mapper = new ResultSetMapper<>();
        return mapper.getGeneratedKeyAndFieldName(dbTableClass, resultSet);
    }
}
