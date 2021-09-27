package io.github.tobias_z.api;

import io.github.tobias_z.annotations.Table;
import io.github.tobias_z.exceptions.NoGeneratedKeyFound;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

class Insert {
    private final PreparedStatement preparedStatement;
    private final Utils utils;

    public Insert(PreparedStatement preparedStatement, Utils utils) {
        this.preparedStatement = preparedStatement;
        this.utils = utils;
    }

    public <T> T withGeneratedKey(Class<T> dbTableClass)
        throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoGeneratedKeyFound {
        preparedStatement.executeUpdate();
        ResultSet resultSet = preparedStatement.getGeneratedKeys();
        ResultSetMapper<T> mapper = new ResultSetMapper<>();
        Entry<String, Object> entry = mapper.getPrimaryKeyAndFieldName(dbTableClass, resultSet);
        return getByPrimaryKey(dbTableClass, entry, preparedStatement.getConnection());
    }

    private <T> T getByPrimaryKey(Class<T> dbTableClass, Entry<String, Object> keyAndValue,
                                  Connection connection)
            throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Table table = utils.getTableAnnotation(dbTableClass);
        String fieldName = keyAndValue.getKey();
        Object value = keyAndValue.getValue();

        PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM " + table.name() + " WHERE " + fieldName + " = " + value);
        ResultSet resultSet = ps.executeQuery();
        ResultSetMapper<T> mapper = new ResultSetMapper<>();
        return mapper.mapSingleResult(dbTableClass, resultSet);
    }
}
