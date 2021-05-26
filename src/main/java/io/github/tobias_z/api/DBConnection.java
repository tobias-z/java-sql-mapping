package io.github.tobias_z.api;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.Database;

public interface DBConnection {

    /**
     *
     * @param dbConfig Your db configuration.
     * @return The database used to call methods for persistence and selection
     */
    static Database createDatabase(DBConfig dbConfig) {
        return new DatabaseRepository(dbConfig);
    }

}
