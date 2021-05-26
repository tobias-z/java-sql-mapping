package io.github.tobias_z.api.database;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.Database;
import io.github.tobias_z.api.SQLQuery;
import io.github.tobias_z.api.connection.DBConfigArgumentProvider;
import io.github.tobias_z.entities.NoIncrement;
import io.github.tobias_z.entities.Role;
import io.github.tobias_z.entities.User;
import io.github.tobias_z.exceptions.DatabaseException;
import io.github.tobias_z.utils.BeforeEachSetup;
import io.github.tobias_z.utils.SetupIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class InsertTest extends SetupIntegrationTests {

    SQLQuery insertUserQuery;
    SQLQuery insertNoIncrementQuery;
    String username = "Bob";
    String message = "Hello Bob";

    private static Database DB;

    private final BeforeEachSetup beforeEach = (database) -> {
        insertUserQuery = new SQLQuery("INSERT INTO users (name, active, role) VALUES (:name, :active, :role)")
            .addParameter("name", username)
            .addParameter("active", false)
            .addParameter("role", Role.ADMIN);
        insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message, role) VALUES (:message, :role)")
            .addParameter("message", message)
            .addParameter("role", Role.EMPLOYEE);
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
        assertEquals(Role.ADMIN, user.getRole());
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
        assertEquals(Role.EMPLOYEE, noIncrement.getRole());
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
