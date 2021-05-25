package com.tobias_z.api;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import com.tobias_z.annotations.Column;
import com.tobias_z.annotations.PrimaryKey;
import com.tobias_z.exceptions.NoGeneratedKeyFound;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

class ResultSetMapper<T> {

    private boolean isBoolean(Field field) {
        return field.getType() == boolean.class || field.getType() == Boolean.class;
    }

    private boolean isInteger(Field field) {
        return field.getType() == Integer.class || field.getType() == int.class;
    }

    private Object getValue(Object value, Field field) {
        Object toReturn = value;
        if (isBoolean(field)) {
            if (toReturn.equals("1")) {
                toReturn = true;
            } else {
                toReturn = false;
            }
        } else if (isInteger(field)) {
            toReturn = Integer.parseInt(String.valueOf(value));
        }
        return toReturn;
    }

    private T getOneResult(Class<T> clazz, ResultSet resultSet)
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        T dto = clazz.getConstructor().newInstance();
        Field[] fieldList = clazz.getDeclaredFields();
        for (Field field : fieldList) {
            Column col = field.getAnnotation(Column.class);
            if (col != null) {
                field.setAccessible(true);
                Object value = resultSet.getString(col.name());
                value = getValue(value, field);
                field.set(dto, value);
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

    public List<T> mapListOfResults(Class<T> clazz, ResultSet resultSet)
        throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<T> tList = new ArrayList<>();
        while (resultSet.next()) {
            tList.add(getOneResult(clazz, resultSet));
        }
        return tList;
    }

    public Entry<String, Object> getPrimaryKeyAndFieldName(Class<T> clazz, ResultSet resultSet)
        throws SQLException, NoGeneratedKeyFound {
        if (resultSet.next()) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                PrimaryKey key = field.getAnnotation(PrimaryKey.class);
                Column column = field.getAnnotation(Column.class);
                if (key != null && column != null) {
                    int id = resultSet.getInt(1);
                    return new SimpleEntry<>(column.name(), id);
                }
            }
        }
        throw new NoGeneratedKeyFound("Either no generated key was found, or it does not have a column name");
    }

}
