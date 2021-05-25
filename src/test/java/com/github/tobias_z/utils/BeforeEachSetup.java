package com.github.tobias_z.utils;

import com.github.tobias_z.Database;

@FunctionalInterface
public interface BeforeEachSetup {

    void apply(Database database) throws Exception;

}
