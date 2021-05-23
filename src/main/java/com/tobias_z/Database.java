package com.tobias_z;

import com.tobias_z.exceptions.DatabaseException;
import com.tobias_z.exceptions.NoGeneratedKeyFound;
import com.tobias_z.exceptions.NoPrimaryKeyFound;
import com.tobias_z.exceptions.NoTableFound;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface Database {

    <T> T insert(SQLQuery query, Class<T> dbTableClass)
        throws DatabaseException, NoGeneratedKeyFound, NoPrimaryKeyFound, NoTableFound;

    void insert(SQLQuery query) throws DatabaseException;

    void update(SQLQuery query) throws DatabaseException;

    <T> T update(SQLQuery query, Class<T> dbTableClass)
        throws DatabaseException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    <T, PrimaryKey> T get(PrimaryKey primaryKey, Class<T> dbTableClass)
        throws DatabaseException, NoTableFound, NoGeneratedKeyFound;

    <T> List<T> getAll(Class<T> dbTableClass) throws DatabaseException, NoTableFound;

    <T> List<T> select(SQLQuery query, Class<T> dbTableClass) throws DatabaseException;

    <T, PrimaryKey> void delete(PrimaryKey primaryKey, Class<T> dbTableClass) throws DatabaseException;

    void delete(SQLQuery query) throws DatabaseException;


}
