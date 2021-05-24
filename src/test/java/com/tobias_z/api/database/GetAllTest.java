package com.tobias_z.api.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tobias_z.DBConfig;
import com.tobias_z.Database;
import com.tobias_z.SQLQuery;
import com.tobias_z.utils.BeforeEachSetup;
import com.tobias_z.api.connection.DBConfigArgumentProvider;
import com.tobias_z.utils.SetupIntegrationTests;
import com.tobias_z.entities.NoIncrement;
import com.tobias_z.entities.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class GetAllTest extends SetupIntegrationTests {

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
        database.insert(insertUserQuery);
        database.insert(insertUserQuery);
        database.insert(insertUserQuery);
        database.insert(insertNoIncrementQuery);
    };

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return a list of users")
    void shouldReturnAListOfUsers(DBConfig dbConfig, String dbName, String migrateFile) throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        List<User> users = DB.getAll(User.class);
        assertEquals(3, users.size());
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return a list of no increments")
    void shouldReturnAListOfNoIncrements(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        List<NoIncrement> noIncrements = DB.getAll(NoIncrement.class);
        assertEquals(1, noIncrements.size());
    }

}
