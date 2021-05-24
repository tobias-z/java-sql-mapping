package com.tobias_z.utils;

import com.tobias_z.Database;

@FunctionalInterface
public interface BeforeEachSetup {

    void apply(Database database) throws Exception;

}
