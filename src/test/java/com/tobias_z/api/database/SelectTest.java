package com.tobias_z.api.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tobias_z.DBConfig;
import com.tobias_z.Database;
import com.tobias_z.SQLQuery;
import com.tobias_z.utils.BeforeEachSetup;
import com.tobias_z.api.connection.DBConfigArgumentProvider;
import com.tobias_z.utils.SetupIntegrationTests;
import com.tobias_z.entities.NoIncrement;
import com.tobias_z.entities.User;
import com.tobias_z.exceptions.DatabaseException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class SelectTest extends SetupIntegrationTests {

    SQLQuery insertUserQuery;
    String username = "Bob";

    private static Database DB;

    private final BeforeEachSetup beforeEach = (database) -> {
        insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
            .addParameter("name", username);
        database.insert(insertUserQuery);
        database.insert(insertUserQuery);
        database.insert(insertUserQuery);
    };

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return a list of users with from name")
    void shouldReturnAListOfUsersWithFromName(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        SQLQuery query = new SQLQuery("SELECT * FROM users WHERE name = :name")
            .addParameter("name", username);
        List<User> users = DB.select(query, User.class);
        assertEquals(3, users.size());
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should throw exception if query fails")
    void shouldThrowExceptionIfQueryFails(DBConfig dbConfig, String dbName, String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        SQLQuery query = new SQLQuery("SELECT * FROM fails");
        assertThrows(DatabaseException.class, () -> DB.select(query, NoIncrement.class));
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    @DisplayName("should return empty list of no increments when none exist")
    void shouldReturnEmptyListOfNoIncrementsWhenNoneExist(DBConfig dbConfig, String dbName,
        String migrateFile)
        throws Exception {
        DB = setupTest(dbConfig, beforeEach, migrateFile);
        SQLQuery query = new SQLQuery("SELECT * FROM no_increment");
        List<NoIncrement> noIncrements = DB.select(query, NoIncrement.class);
        assertNotNull(noIncrements);
        assertEquals(0, noIncrements.size());
    }

}