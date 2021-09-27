package io.github.tobias_z.api.database;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.Database;
import io.github.tobias_z.api.connection.DBConfigArgumentProvider;
import io.github.tobias_z.entities.NoIncrement;
import io.github.tobias_z.entities.Role;
import io.github.tobias_z.entities.User;
import io.github.tobias_z.utils.BeforeEachSetup;
import io.github.tobias_z.utils.DBStatements;
import io.github.tobias_z.utils.SetupIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateTest extends SetupIntegrationTests {

    String username = "Bob";
    String message = "Hello Bob";

    String newName = "Updated Bob";
    String newMessage = "This is an updated message";

    User user;

    private final BeforeEachSetup beforeEach = (database) -> {
        user = database.insert(DBStatements.insertUser(username, true), User.class);
        database.executeQuery(DBStatements.insertUser(username, true));
        database.executeQuery(DBStatements.insertUser(username, true));
        database.executeQuery(DBStatements.insertNoIncrement(message));
    };

    private PreparedStatement updateUserStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE users SET name = ?, active = ?, role = ? WHERE id = ?");
        ps.setString(1, newName);
        ps.setBoolean(2, false);
        ps.setString(3, Role.EMPLOYEE.name());
        ps.setInt(4, user.getId());
        return ps;
    }

    private PreparedStatement updateNoIncrementStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("UPDATE no_increment SET message = ?, role = ? WHERE message = ?");
        ps.setString(1, newMessage);
        ps.setString(2, Role.EMPLOYEE.name());
        ps.setString(3, message);
        return ps;
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should update a users name to a different value")
    void shouldUpdateAUsersNameToADifferentValue(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        db.executeQuery(this::updateUserStatement);
        User updatedUser = db.get(user.getId(), User.class);
        assertEquals(user.getId(), updatedUser.getId());
        assertEquals(Role.EMPLOYEE, updatedUser.getRole());
        assertEquals(newName, updatedUser.getName());
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should not throw exception when updating user")
    void shouldNotThrowExceptionWhenUpdatingUser(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        assertDoesNotThrow(() -> db.executeQuery(this::updateUserStatement));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should not throw exception when updating no increment")
    void shouldNotThrowExceptionWhenUpdatingNoIncrement(DBConfig dbConfig, String dbName,
        String migrateFile)
        throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        assertDoesNotThrow(() -> db.executeQuery(this::updateNoIncrementStatement));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should be able to update primary key of string")
    void shouldBeAbleToUpdatePrimaryKeyOfString(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        db.executeQuery(this::updateNoIncrementStatement);
        NoIncrement updatedNoIncrement = db.get(newMessage, NoIncrement.class);
        assertEquals(newMessage, updatedNoIncrement.getMessage());
        assertEquals(Role.EMPLOYEE, updatedNoIncrement.getRole());
        assertNotEquals(message, updatedNoIncrement.getMessage());
    }

}
