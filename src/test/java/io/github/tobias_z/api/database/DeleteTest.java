package io.github.tobias_z.api.database;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.Database;
import io.github.tobias_z.SQLQuery;
import io.github.tobias_z.api.connection.DBConfigArgumentProvider;
import io.github.tobias_z.entities.Role;
import io.github.tobias_z.entities.User;
import io.github.tobias_z.exceptions.DatabaseException;
import io.github.tobias_z.utils.BeforeEachSetup;
import io.github.tobias_z.utils.SetupIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class DeleteTest extends SetupIntegrationTests {

    private static Database DB;

    SQLQuery insertUserQuery;
    SQLQuery insertNoIncrementQuery;
    String username = "Bob";
    String message = "Hello Bob";

    User user;

    private final BeforeEachSetup beforeEach = (database) -> {
        insertUserQuery = new SQLQuery("INSERT INTO users (name, active, role) VALUES (:name, :active, :role)")
            .addParameter("name", username)
            .addParameter("active", false)
            .addParameter("role", Role.ADMIN);
        insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message, role) VALUES (:message, :role)")
            .addParameter("message", message)
            .addParameter("role", Role.EMPLOYEE);
        user = database.insert(insertUserQuery, User.class);
        database.insert(insertUserQuery);
        database.insert(insertUserQuery);
        database.insert(insertNoIncrementQuery);
    };

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should not throw when deleting user")
    void shouldNotThrowWhenDeletingUser(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        assertDoesNotThrow(() -> DB.delete(user.getId(), User.class));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should not be able to find user when deleted")
    void shouldNotBeAbleToFindUserWhenDeleted(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        DB.delete(user.getId(), User.class);
        assertThrows(DatabaseException.class, () -> DB.get(user.getId(), User.class));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should delete user with SQL query")
    void shouldDeleteUserWithSqlQuery(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        SQLQuery deleteUserQuery = new SQLQuery("DELETE FROM users WHERE id = :id")
            .addParameter("id", user.getId());
        assertDoesNotThrow(() -> DB.delete(deleteUserQuery));
    }

}
