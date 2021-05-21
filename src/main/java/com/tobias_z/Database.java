package com.tobias_z;

import com.tobias_z.exceptions.DatabaseException;
import com.tobias_z.exceptions.NoGeneratedKeyFound;

public interface Database {

    <T> Inserted<T> insert(SQLQuery query, Class<T> dbTableClass) throws DatabaseException, NoGeneratedKeyFound;
    void insert(SQLQuery query) throws DatabaseException;

    SQLQuery createSQLQuery(String sql);

}
