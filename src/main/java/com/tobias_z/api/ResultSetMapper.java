package com.tobias_z.api;

import com.tobias_z.annotations.Column;
import com.tobias_z.annotations.GeneratedKey;
import com.tobias_z.exceptions.DatabaseException;
import com.tobias_z.exceptions.NoGeneratedKeyFound;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResultSetMapper<T> {

    private T getOneResult(Class<T> clazz, ResultSet resultSet)
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        T dto = clazz.getConstructor().newInstance();
        Field[] fieldList = clazz.getDeclaredFields();
        for (Field field : fieldList) {
            Column col = field.getAnnotation(Column.class);
            if (col != null) {
                field.setAccessible(true);
                String value = resultSet.getString(col.name());
                field.set(dto, field.getType().getConstructor(String.class).newInstance(value));
            }
        }
        return dto;
    }

    public T mapSingleResult(Class<T> clazz, ResultSet resultSet)
        throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (resultSet.next()) {
            return getOneResult(clazz, resultSet);
        }
        throw new SQLException("Unable to find result from query");
    }

    public LinkedHashMap<String, Integer> getGeneratedKeyAndFieldName(Class<T> clazz, ResultSet resultSet)
        throws SQLException, NoGeneratedKeyFound {
        if (resultSet.next()) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                GeneratedKey key = field.getAnnotation(GeneratedKey.class);
                Column column = field.getAnnotation(Column.class);
                if (key != null && column != null) {
                    int id = resultSet.getInt(1);
                    LinkedHashMap<String, Integer> keyAndName = new LinkedHashMap<>();
                    keyAndName.put(column.name(), id);
                    return keyAndName;
                }
            }
        }
        throw new NoGeneratedKeyFound("Either no generated key was found, or it does not have a column name");
    }

}
