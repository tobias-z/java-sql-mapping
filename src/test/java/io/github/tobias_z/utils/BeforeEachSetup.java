package io.github.tobias_z.utils;

import io.github.tobias_z.Database;

@FunctionalInterface
public interface BeforeEachSetup {

    void apply(Database database) throws Exception;

}
