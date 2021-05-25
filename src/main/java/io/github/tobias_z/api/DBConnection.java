package io.github.tobias_z.api;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.Database;

public interface DBConnection {

    static Database createDatabase(DBConfig dbConfig) {
        return new DatabaseRepository(dbConfig);
    }

}
