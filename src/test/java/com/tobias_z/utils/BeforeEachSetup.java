package com.tobias_z.utils;

import com.tobias_z.Database;
import com.tobias_z.exceptions.DatabaseException;

@FunctionalInterface
public interface BeforeEachSetup {
    void apply(Database database) throws Exception;
}
