package com.tobias_z;

import com.tobias_z.exceptions.DatabaseException;
import com.tobias_z.exceptions.NoGeneratedKeyFound;
import com.tobias_z.exceptions.NoTableFound;
import java.util.List;

public interface Database {

    <T> Inserted<T> insert(SQLQuery query, Class<T> dbTableClass)
        throws DatabaseException, NoGeneratedKeyFound;

    void insert(SQLQuery query) throws DatabaseException;

    <T, PrimaryKey> T select(PrimaryKey primaryKey, Class<T> dbTableClass) throws DatabaseException, NoTableFound, NoGeneratedKeyFound;

    <T> List<T> select(SQLQuery query, Class<T> dbTableClass) throws DatabaseException;

    <T> List<T> selectAll(Class<T> dbTableClass) throws DatabaseException, NoTableFound;

}
