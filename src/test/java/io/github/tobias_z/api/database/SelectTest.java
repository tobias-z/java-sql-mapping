package io.github.tobias_z.api.database;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.Database;
import io.github.tobias_z.api.connection.DBConfigArgumentProvider;
import io.github.tobias_z.entities.NoIncrement;
import io.github.tobias_z.entities.User;
import io.github.tobias_z.exceptions.DatabaseException;
import io.github.tobias_z.utils.BeforeEachSetup;
import io.github.tobias_z.utils.DBStatements;
import io.github.tobias_z.utils.SetupIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SelectTest extends SetupIntegrationTests {

    String username = "Bob";

    private final BeforeEachSetup beforeEach = (database) -> {
        database.executeQuery(DBStatements.insertUser(username, false));
        database.executeQuery(DBStatements.insertUser(username, false));
        database.executeQuery(DBStatements.insertUser(username, false));
    };

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return a list of users with from name")
    void shouldReturnAListOfUsersWithFromName(DBConfig dbConfig, String dbName, String migrateFile)
            throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        List<User> users = db.select(connection -> {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE name = ?");
            ps.setString(1, username);
            return ps;
        }, User.class);
        assertEquals(3, users.size());
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should throw exception if query fails")
    void shouldThrowExceptionIfQueryFails(DBConfig dbConfig, String dbName, String migrateFile)
            throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        assertThrows(DatabaseException.class, () ->
                db.select(connection -> connection.prepareStatement("SELECT * FROM fails"), NoIncrement.class));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return empty list of no increments when none exist")
    void shouldReturnEmptyListOfNoIncrementsWhenNoneExist(DBConfig dbConfig, String dbName,
                                                          String migrateFile)
            throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        List<NoIncrement> noIncrements = db.select(connection ->
                connection.prepareStatement("SELECT * FROM no_increment"), NoIncrement.class);
        assertNotNull(noIncrements);
        assertEquals(0, noIncrements.size());
    }

}
