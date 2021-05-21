package com.tobias_z;

import com.tobias_z.exceptions.DatabaseException;
import com.tobias_z.exceptions.NoTableFound;

public interface Inserted<T> {

    T getGeneratedEntity() throws NoTableFound, DatabaseException;

}
