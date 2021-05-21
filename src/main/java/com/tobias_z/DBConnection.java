package com.tobias_z;

import com.tobias_z.api.DatabaseRepository;

public interface DBConnection {

    static Database createConnection(DBConfig dbConfig) {
        return new DatabaseRepository(dbConfig);
    }

}
