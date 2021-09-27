package io.github.tobias_z.api.database;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.Database;
import io.github.tobias_z.api.connection.DBConfigArgumentProvider;
import io.github.tobias_z.entities.User;
import io.github.tobias_z.exceptions.DatabaseException;
import io.github.tobias_z.utils.BeforeEachSetup;
import io.github.tobias_z.utils.DBStatements;
import io.github.tobias_z.utils.SetupIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteTest extends SetupIntegrationTests {
    String username = "Bob";
    String message = "Hello Bob";
    User user;

    private final BeforeEachSetup beforeEach = (database) -> {
        user = database.insert(DBStatements.insertUser(username, false), User.class);
        database.executeQuery(DBStatements.insertUser(username, false));
        database.executeQuery(DBStatements.insertUser(username, false));
        database.executeQuery(DBStatements.insertNoIncrement(message));
    };

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should not throw when deleting user")
    void shouldNotThrowWhenDeletingUser(DBConfig dbConfig, String dbName, String migrateFile)
            throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        assertDoesNotThrow(() -> db.delete(user.getId(), User.class));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should not be able to find user when deleted")
    void shouldNotBeAbleToFindUserWhenDeleted(DBConfig dbConfig, String dbName, String migrateFile)
            throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        db.delete(user.getId(), User.class);
        assertThrows(DatabaseException.class, () -> db.get(user.getId(), User.class));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should delete user with SQL query")
    void shouldDeleteUserWithSqlQuery(DBConfig dbConfig, String dbName, String migrateFile)
            throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        assertDoesNotThrow(() -> db.executeQuery(connection -> {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE id = ?");
            ps.setInt(1, user.getId());
            return ps;
        }));
    }

}
