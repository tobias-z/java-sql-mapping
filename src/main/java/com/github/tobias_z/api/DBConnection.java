package com.github.tobias_z.api;

import com.github.tobias_z.DBConfig;
import com.github.tobias_z.Database;

public interface DBConnection {

    static Database createDatabase(DBConfig dbConfig) {
        return new DatabaseRepository(dbConfig);
    }

}
