package io.github.tobias_z;

import io.github.tobias_z.exceptions.DatabaseException;
import io.github.tobias_z.exceptions.NoGeneratedKeyFound;
import io.github.tobias_z.exceptions.NoPrimaryKeyFound;
import io.github.tobias_z.exceptions.NoTableFound;
import java.util.List;

public interface Database {

    /**
     *
     * @param statement
     * @param dbTableClass The class that you want to be returned.
     * @return Your generated row as an object.
     * @throws DatabaseException If something went wrong with the database transaction.
     * @throws NoPrimaryKeyFound If no primary key is found. It is required to have a primary key.
     * @throws NoTableFound      If no table annotation is found. It is required to have a Table annotation.
     */
    <T> T insert(DBStatement statement, Class<T> dbTableClass)
        throws DatabaseException, NoGeneratedKeyFound, NoPrimaryKeyFound, NoTableFound;

    /**
     * @param primaryKey   The primary key
     * @param dbTableClass The class that you want to be returned.
     * @return The object corresponding with your primary key's table
     * @throws DatabaseException If something went wrong with the database transaction.
     * @throws NoPrimaryKeyFound If no primary key is found. It is required to have a primary key.
     * @throws NoTableFound      If no table annotation is found. It is required to have a Table annotation.
     */
    <T, PrimaryKey> T get(PrimaryKey primaryKey, Class<T> dbTableClass)
        throws DatabaseException, NoTableFound;

    /**
     * @param dbTableClass The class that you want to be returned.
     * @return All of the rows corresponding with your selected class
     * @throws DatabaseException If something went wrong with the database transaction.
     * @throws NoTableFound      If no table annotation is found. It is required to have a Table annotation.
     */
    <T> List<T> getAll(Class<T> dbTableClass) throws DatabaseException, NoTableFound;

    /**
     *
     * @param statement
     * @param dbTableClass The class that you want to be returned.
     * @return A list of your selected class
     * @throws DatabaseException If something went wrong with the database transaction.
     */
    <T> List<T> select(DBStatement statement, Class<T> dbTableClass) throws DatabaseException;

    /**
     * @param primaryKey   The primary key
     * @param dbTableClass The class that you want to be returned.
     * @throws DatabaseException If something went wrong with the database transaction.
     */
    <T, PrimaryKey> void delete(PrimaryKey primaryKey, Class<T> dbTableClass) throws DatabaseException;

    /**
     *
     * @param statement@throws DatabaseException If something went wrong with the database transaction.
     */
    void executeQuery(DBStatement statement) throws DatabaseException;
}
