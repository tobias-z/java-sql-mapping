package io.github.tobias_z.api;

import io.github.tobias_z.annotations.AutoIncremented;
import io.github.tobias_z.annotations.Column;
import io.github.tobias_z.annotations.PrimaryKey;
import io.github.tobias_z.annotations.Table;
import io.github.tobias_z.exceptions.NoGeneratedKeyFound;
import io.github.tobias_z.exceptions.NoPrimaryKeyFound;
import io.github.tobias_z.exceptions.NoTableFound;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

class Utils {

    <T> T getPrimaryKeyStringForSQL(T primaryKey) {
        try {
            Integer.parseInt(String.valueOf(primaryKey));
        } catch (NumberFormatException e) {
            primaryKey = (T) ("'" + primaryKey + "'");
        }
        return primaryKey;
    }

    <T> Entry<String, Object> getPrimaryKeyAndValue(Class<T> dbTableClass, ResultSet resultSet)
            throws NoPrimaryKeyFound, SQLException {
        Field[] fields = dbTableClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                String key = field.getAnnotation(Column.class).name();
                Object value = resultSet.getObject(i + 1);
                try {
                    Integer.parseInt(String.valueOf(value));
                } catch (NumberFormatException e) {
                    value = "'" + value + "'";
                }
                return new SimpleEntry<>(key, value);
            }
        }
        throw new NoPrimaryKeyFound("Did not find a a primary key on class: " + dbTableClass.getName());
    }

    <T> boolean isWithGeneratedKey(Class<T> dbTableClass) {
        Field[] fields = dbTableClass.getDeclaredFields();
        for (Field field : fields) {
            AutoIncremented autoIncremented = field.getAnnotation(AutoIncremented.class);
            if (autoIncremented != null) {
                return true;
            }
        }
        return false;
    }

    String getTinyIntIfBooleanType(String value) {
        if (value.equals("true") || value.equals("True")) {
            return "1";
        } else if (value.equals("false") || value.equals("False")) {
            return "0";
        }
        return value;
    }

    <T> Entry<String, Object> updateValueIfSettingPrimayKey(Class<T> dbTableClass, PreparedStatement ps, ResultSet resultSet, Entry<String, Object> keyAndValue) throws SQLException {
        Object foundValue = keyAndValue.getValue();
        Field[] fields = dbTableClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                String primaryKeyName = field.getAnnotation(Column.class).name();
                if (ps.toString().contains("SET " + primaryKeyName)) {
                    foundValue = resultSet.getObject(i + 1);
                    try {
                        Integer.parseInt(String.valueOf(foundValue));
                    } catch (NumberFormatException e) {
                        foundValue = "'" + foundValue + "'";
                    }
                }
            }
        }
        return new SimpleEntry<>(keyAndValue.getKey(), foundValue);
    }

    private String substringAfter(String str, String separator) {
        int pos = str.indexOf(separator);
        if (pos == 0) {
            return str;
        }
        return str.substring(pos + separator.length());
    }

    <T> Column getPrimaryKeyColumn(Class<T> dbTableClass) throws NoGeneratedKeyFound {
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

    <T> Table getTableAnnotation(Class<T> dbTableClass) throws NoTableFound {
        Table table = dbTableClass.getAnnotation(Table.class);
        if (table == null) {
            throw new NoTableFound("Did not find a Table name for: " + dbTableClass.getName());
        }
        return table;
    }
}
