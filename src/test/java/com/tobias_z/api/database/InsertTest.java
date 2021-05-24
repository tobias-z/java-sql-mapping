package com.tobias_z.api.database;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import com.tobias_z.DBConfig;
import com.tobias_z.Database;
import com.tobias_z.SQLQuery;
import com.tobias_z.utils.BeforeEachSetup;
import com.tobias_z.api.connection.DBConfigArgumentProvider;
import com.tobias_z.utils.SetupIntegrationTests;
import com.tobias_z.entities.NoIncrement;
import com.tobias_z.entities.User;
import com.tobias_z.exceptions.DatabaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class InsertTest extends SetupIntegrationTests {

    SQLQuery insertUserQuery;
    SQLQuery insertNoIncrementQuery;
    String username = "Bob";
    String message = "Hello Bob";

    private static Database DB;

    private final BeforeEachSetup beforeEach = (database) -> {
        insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
            .addParameter("name", username);
        insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message) VALUES (:message)")
            .addParameter("message", message);
    };

    @ParameterizedTest(name = "{1}")
    @DisplayName("should not throw exception")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    void shouldNotThrowException(DBConfig dbConfig, String dbName, String migrateFile) throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        assertDoesNotThrow(() -> DB.insert(insertUserQuery));
    }

    @DisplayName("should return a user from the auto incremented primary key")
    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    void shouldReturnAUserFromTheAutoIncrementedPrimaryKey(DBConfig dbConfig, String dbName,
        String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        User user = DB.insert(insertUserQuery, User.class);
        assertEquals(username, user.getName());
        assertNotNull(user.getId());
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return a NoIncrement with no auto incremented primary key")
    void shouldReturnANoIncrementWithNoAutoIncrementedPrimaryKey(DBConfig dbConfig, String dbName,
        String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        NoIncrement noIncrement = DB.insert(insertNoIncrementQuery, NoIncrement.class);
        assertEquals(message, noIncrement.getMessage());
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should throw exception if primary key already exists")
    void shouldThrowExceptionIfPrimaryKeyAlreadyExists(DBConfig dbConfig, String dbName,
        String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        DB.insert(insertNoIncrementQuery);
        assertThrows(DatabaseException.class, () -> DB.insert(insertNoIncrementQuery));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should throw exception if incorrect insert query")
    void shouldThrowExceptionIfIncorrectInsertQuery(DBConfig dbConfig, String dbName, String migrateFile) throws Exception {
        DB = setupTest(dbConfig, migrateFile);
        SQLQuery failingQuery = new SQLQuery("INSERT INTO (dsa)");
        assertThrows(DatabaseException.class, () -> DB.insert(failingQuery, User.class));
    }

}
