package io.github.tobias_z.api.database;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.Database;
import io.github.tobias_z.api.connection.DBConfigArgumentProvider;
import io.github.tobias_z.entities.Role;
import io.github.tobias_z.entities.User;
import io.github.tobias_z.exceptions.DatabaseException;
import io.github.tobias_z.utils.DBStatements;
import io.github.tobias_z.utils.SetupIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.*;

public class InsertTest extends SetupIntegrationTests {

    String username = "Bob";
    String message = "Hello Bob";

    @ParameterizedTest(name = "{1}")
    @DisplayName("should not throw exception")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    void shouldNotThrowException(DBConfig dbConfig, String dbName, String migrateFile) throws Exception {
        Database db = setupTest(dbConfig, migrateFile);
        assertDoesNotThrow(() -> db.executeQuery(DBStatements.insertUser(username, false)));
    }

    @DisplayName("should return a user from the auto incremented primary key")
    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    void shouldReturnAUserFromTheAutoIncrementedPrimaryKey(DBConfig dbConfig, String dbName,
        String migrateFile)
        throws Exception {
        Database db = setupTest(dbConfig, migrateFile);
        User user = db.insert(DBStatements.insertUser(username, false), User.class);
        assertEquals(username, user.getName());
        assertEquals(Role.ADMIN, user.getRole());
        assertNotNull(user.getId());
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should throw exception if primary key already exists")
    void shouldThrowExceptionIfPrimaryKeyAlreadyExists(DBConfig dbConfig, String dbName,
        String migrateFile)
        throws Exception {
        Database db = setupTest(dbConfig, migrateFile);
        db.executeQuery(DBStatements.insertNoIncrement(message));
        assertThrows(DatabaseException.class, () -> db.executeQuery(DBStatements.insertNoIncrement(message)));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should throw exception if incorrect insert query")
    void shouldThrowExceptionIfIncorrectInsertQuery(DBConfig dbConfig, String dbName, String migrateFile) throws Exception {
        Database db = setupTest(dbConfig, migrateFile);
        assertThrows(DatabaseException.class, () ->
                db.executeQuery(connection -> connection.prepareStatement("INSERT INTO users")));
    }

}
