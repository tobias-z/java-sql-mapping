package io.github.tobias_z.api.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.Database;
import io.github.tobias_z.api.SQLQuery;
import io.github.tobias_z.entities.Role;
import io.github.tobias_z.utils.BeforeEachSetup;
import io.github.tobias_z.api.connection.DBConfigArgumentProvider;
import io.github.tobias_z.utils.DBStatements;
import io.github.tobias_z.utils.SetupIntegrationTests;
import io.github.tobias_z.entities.NoIncrement;
import io.github.tobias_z.entities.User;
import io.github.tobias_z.exceptions.DatabaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class GetTest extends SetupIntegrationTests {

    String username = "Bob";
    String message = "Hello Bob";
    User user;

    private final BeforeEachSetup beforeEach = (database) -> {
        user = database.insert(DBStatements.insertUser(username, false), User.class);
        database.executeQuery(DBStatements.insertNoIncrement(message));
    };

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return a user")
    void shouldReturnAUser(DBConfig dbConfig, String dbName, String migrateFile) throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        User foundUser = db.get(user.getId(), User.class);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getName(), foundUser.getName());
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should throw exception if incorrect primary key")
    void shouldThrowExceptionIfIncorrectPrimaryKey(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        assertThrows(DatabaseException.class, () -> db.get(10, User.class));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return a NoIncrement")
    void shouldReturnANoIncrement(DBConfig dbConfig, String dbName, String migrateFile) throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        NoIncrement noIncrement = db.get(message, NoIncrement.class);
        assertEquals(message, noIncrement.getMessage());
    }

}
