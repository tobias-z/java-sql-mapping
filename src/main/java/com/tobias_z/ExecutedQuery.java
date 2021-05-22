package com.tobias_z;

import com.tobias_z.exceptions.DatabaseException;
import com.tobias_z.exceptions.NoTableFound;

public interface ExecutedQuery<T> {

    T getGeneratedEntity() throws NoTableFound, DatabaseException;

}
