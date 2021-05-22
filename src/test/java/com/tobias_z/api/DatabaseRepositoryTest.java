package com.tobias_z.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import com.tobias_z.DBConfig;
import com.tobias_z.Database;
import com.tobias_z.SQLQuery;
import com.tobias_z.api.connection.MySQLTestDBConfig;
import com.tobias_z.api.connection.SetupIntegrationTests;
import com.tobias_z.entities.NoIncrement;
import com.tobias_z.entities.User;
import com.tobias_z.exceptions.DatabaseException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class DatabaseRepositoryTest extends SetupIntegrationTests {

    private static Database DB;

    @BeforeEach
    void setUp() {
        DBConfig dbConfig = new MySQLTestDBConfig();
        DB = new DatabaseRepository(dbConfig);
        runTestDatabaseMigration(dbConfig);
    }

    @Test
    @DisplayName("database is up and running")
    void databaseIsUpAndRunning() {
        assertNotNull(DB);
    }

    @Nested
    @DisplayName("insert")
    class Insert {

        SQLQuery insertUserQuery;
        SQLQuery insertNoIncrementQuery;
        String username = "Bob";
        String message = "Hello Bob";

        @BeforeEach
        void setUp() {
            insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
                .addParameter("name", username);
            insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message) VALUES (:message)")
                .addParameter("message", message);
        }

        @Test
        @DisplayName("should not throw exception")
        void shouldNotThrowException() throws Exception {
            assertDoesNotThrow(() -> DB.insert(insertUserQuery));
        }

        @Test
        @DisplayName("should return a user from the auto incremented primary key")
        void shouldReturnAUserFromTheAutoIncrementedPrimaryKey() throws Exception {
            User user = DB.insert(insertUserQuery, User.class).getGeneratedEntity();
            assertEquals(username, user.getName());
            assertNotNull(user.getId());
        }

        @Test
        @DisplayName("should return a NoIncrement with no auto incremented primary key")
        void shouldReturnANoIncrementWithNoAutoIncrementedPrimaryKey() throws Exception {
            NoIncrement noIncrement = DB.insert(insertNoIncrementQuery, NoIncrement.class).getGeneratedEntity();
            assertEquals(message, noIncrement.getMessage());
        }

        @Test
        @DisplayName("should throw exception if primary key already exists")
        void shouldThrowExceptionIfPrimaryKeyAlreadyExists() throws Exception {
            DB.insert(insertNoIncrementQuery);
            assertThrows(DatabaseException.class, () -> DB.insert(insertNoIncrementQuery));
        }

    }

    @Nested
    @DisplayName("get")
    class Get {
        SQLQuery insertUserQuery;
        SQLQuery insertNoIncrementQuery;
        String username = "Bob";
        String message = "Hello Bob";

        @BeforeEach
        void setUp() throws Exception {
            insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
                .addParameter("name", username);
            insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message) VALUES (:message)")
                .addParameter("message", message);
            DB.insert(insertUserQuery);
            DB.insert(insertNoIncrementQuery);
        }

        @Test
        @DisplayName("should return a user")
        void shouldReturnAUser() throws Exception {
            User user = DB.get(1, User.class);
            assertEquals(1, user.getId());
            assertEquals(username, user.getName());
        }

        @Test
        @DisplayName("should throw exception if incorrect primary key")
        void shouldThrowExceptionIfIncorrectPrimaryKey() {
            assertThrows(DatabaseException.class, () -> DB.get(10, User.class));
        }

        @Test
        @DisplayName("should return a NoIncrement")
        void shouldReturnANoIncrement() throws Exception {
            NoIncrement noIncrement = DB.get(message, NoIncrement.class);
            assertEquals(message, noIncrement.getMessage());
        }

    }

    @Nested
    @DisplayName("get all")
    class GetAll {

        SQLQuery insertUserQuery;
        SQLQuery insertNoIncrementQuery;
        String username = "Bob";
        String message = "Hello Bob";

        @BeforeEach
        void setUp() throws Exception {
            insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
                .addParameter("name", username);
            insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message) VALUES (:message)")
                .addParameter("message", message);
            DB.insert(insertUserQuery);
            DB.insert(insertUserQuery);
            DB.insert(insertUserQuery);
            DB.insert(insertNoIncrementQuery);
        }

        @Test
        @DisplayName("should return a list of users")
        void shouldReturnAListOfUsers() throws Exception {
            List<User> users = DB.getAll(User.class);
            assertEquals(3, users.size());
        }

        @Test
        @DisplayName("should return a list of no increments")
        void shouldReturnAListOfNoIncrements() throws Exception {
            List<NoIncrement> noIncrements = DB.getAll(NoIncrement.class);
            assertEquals(1, noIncrements.size());
        }

    }

    @Nested
    @DisplayName("select")
    class Select {

        SQLQuery insertUserQuery;
        String username = "Tobias";

        @BeforeEach
        void setUp() throws Exception {
            insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
                .addParameter("name", username);
            DB.insert(insertUserQuery);
            DB.insert(insertUserQuery);
            DB.insert(insertUserQuery);
        }


        @Test
        @DisplayName("should return a list of users with from name")
        void shouldReturnAListOfUsersWithFromName() throws Exception {
            SQLQuery query = new SQLQuery("SELECT * FROM users WHERE name = :name")
                .addParameter("name", username);
            List<User> users = DB.select(query, User.class);
            assertEquals(3, users.size());
        }

        @Test
        @DisplayName("should throw exception if query fails")
        void shouldThrowExceptionIfQueryFails() {
            SQLQuery query = new SQLQuery("SELECT * FROM fails");
            assertThrows(DatabaseException.class, () -> DB.select(query, NoIncrement.class));
        }

        @Test
        @DisplayName("should return empty list of no increments when none exist")
        void shouldReturnEmptyListOfNoIncrementsWhenNoneExist() throws Exception {
            SQLQuery query = new SQLQuery("SELECT * FROM no_increment");
            List<NoIncrement> noIncrements = DB.select(query, NoIncrement.class);
            assertNotNull(noIncrements);
            assertEquals(0, noIncrements.size());
        }

    }


}