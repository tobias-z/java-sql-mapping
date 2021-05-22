package com.tobias_z;

import com.tobias_z.api.DatabaseRepository;

public interface DBConnection {

    static Database createDatabase(DBConfig dbConfig) {
        return new DatabaseRepository(dbConfig);
    }

}
