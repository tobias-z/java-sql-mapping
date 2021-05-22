package com.tobias_z;

import com.tobias_z.exceptions.DatabaseException;
import com.tobias_z.exceptions.NoGeneratedKeyFound;
import com.tobias_z.exceptions.NoPrimaryKeyFound;
import com.tobias_z.exceptions.NoTableFound;
import java.util.List;

public interface Database {

    <T> ExecutedQuery<T> insert(SQLQuery query, Class<T> dbTableClass)
        throws DatabaseException, NoGeneratedKeyFound, NoPrimaryKeyFound;

    void insert(SQLQuery query) throws DatabaseException;

    <T, PrimaryKey> T get(PrimaryKey primaryKey, Class<T> dbTableClass) throws DatabaseException, NoTableFound, NoGeneratedKeyFound;

    <T> List<T> select(SQLQuery query, Class<T> dbTableClass) throws DatabaseException;

    <T> List<T> getAll(Class<T> dbTableClass) throws DatabaseException, NoTableFound;

}
