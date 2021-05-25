package io.github.tobias_z.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.Database;
import io.github.tobias_z.api.connection.DBConfigArgumentProvider;
import io.github.tobias_z.utils.SetupIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

class DatabaseInitTest extends SetupIntegrationTests {

    @DisplayName("database is up and running")
    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    void databaseIsUpAndRunning(DBConfig dbConfig, String dbName, String migrateFile) {
        Database DB = setupTest(dbConfig, migrateFile);
        assertNotNull(DB);
    }

}