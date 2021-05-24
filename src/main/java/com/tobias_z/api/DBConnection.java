package com.tobias_z.api;

import com.tobias_z.DBConfig;
import com.tobias_z.Database;

public interface DBConnection {

    static Database createDatabase(DBConfig dbConfig) {
        return new DatabaseRepository(dbConfig);
    }

}
