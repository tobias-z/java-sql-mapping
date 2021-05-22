package com.tobias_z.api;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;
import com.tobias_z.SQLQuery;
import com.tobias_z.annotations.AutoIncremented;
import com.tobias_z.annotations.Column;
import com.tobias_z.annotations.PrimaryKey;
import com.tobias_z.annotations.Table;
import com.tobias_z.exceptions.NoGeneratedKeyFound;
import com.tobias_z.exceptions.NoPrimaryKeyFound;
import com.tobias_z.exceptions.NoTableFound;
import java.lang.reflect.Field;

class Utils {

    <K> K getPrimaryKeyStringForSQL(K primaryKey) {
        try {
            Integer.parseInt(String.valueOf(primaryKey));
        } catch (NumberFormatException e) {
            primaryKey = (K) ("'" + primaryKey + "'");
        }
        return primaryKey;
    }

    <T> Pair<String, Object> getPrimaryKeyAndValue(Class<T> dbTableClass, SQLQuery query)
        throws NoPrimaryKeyFound {
        Field[] fields = dbTableClass.getDeclaredFields();
        for (Field field : fields) {
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                String key = field.getAnnotation(Column.class).name();
                Object value = query.getParameters().get(key);
                try {
                    Integer.parseInt(String.valueOf(value));
                } catch (NumberFormatException e) {
                    value = "'" + value + "'";
                }
                return new Pair<>(key, value);
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