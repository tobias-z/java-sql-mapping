package io.github.tobias_z.api.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.DBStatement;
import io.github.tobias_z.entities.Role;
import io.github.tobias_z.entities.User;
import io.github.tobias_z.Database;
import io.github.tobias_z.api.SQLQuery;
import io.github.tobias_z.utils.BeforeEachSetup;
import io.github.tobias_z.api.connection.DBConfigArgumentProvider;
import io.github.tobias_z.utils.DBStatements;
import io.github.tobias_z.utils.SetupIntegrationTests;
import io.github.tobias_z.entities.NoIncrement;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class GetAllTest extends SetupIntegrationTests {

    String username = "Bob";
    String message = "Hello Bob";

    private static Database DB;

    private final BeforeEachSetup beforeEach = (database) -> {
        database.executeQuery(DBStatements.insertUser(username, false));
        database.executeQuery(DBStatements.insertUser(username, false));
        database.executeQuery(DBStatements.insertUser(username, false));
        database.executeQuery(DBStatements.insertNoIncrement(message));
    };

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return a list of users")
    void shouldReturnAListOfUsers(DBConfig dbConfig, String dbName, String migrateFile) throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        List<User> users = db.getAll(User.class);
        assertEquals(3, users.size());
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return a list of no increments")
    void shouldReturnAListOfNoIncrements(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        Database db = setupTest(dbConfig, beforeEach, migrateFile);
        List<NoIncrement> noIncrements = db.getAll(NoIncrement.class);
        assertEquals(1, noIncrements.size());
    }

}
