package com.tobias_z.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tobias_z.DBConfig;
import com.tobias_z.Database;
import com.tobias_z.SQLQuery;
import com.tobias_z.api.connection.SetupIntegrationTests;
import com.tobias_z.entities.NoIncrement;
import com.tobias_z.entities.User;
import com.tobias_z.exceptions.DatabaseException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

class DatabaseRepositoryTest extends SetupIntegrationTests {

    SQLQuery insertUserQuery;
    SQLQuery insertNoIncrementQuery;
    String username = "Bob";
    String message = "Hello Bob";

    private static Database DB;

    private void setupTest(DBConfig dbConfig, VoidFunction func) throws Exception {
        DB = new DatabaseRepository(dbConfig);
        runTestDatabaseMigration(dbConfig);
        func.apply();
    }

    private void setupTest(DBConfig dbConfig) {
        DB = new DatabaseRepository(dbConfig);
        runTestDatabaseMigration(dbConfig);
    }

    @DisplayName("database is up and running")
    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(DBConfigArgumentProvider.class)
    void databaseIsUpAndRunning(DBConfig dbConfig, String dbName) {
        setupTest(dbConfig);
        assertNotNull(DB);
    }

    @Nested
    @DisplayName("insert")
    class Insert {

        private final VoidFunction beforeEach = () -> {
            insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
                .addParameter("name", username);
            insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message) VALUES (:message)")
                .addParameter("message", message);
        };

        @ParameterizedTest(name = "{1}")
        @DisplayName("should return a user from the auto incremented primary key")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        void shouldNotThrowException(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            assertDoesNotThrow(() -> DB.insert(insertUserQuery));
        }

        @DisplayName("should return a user from the auto incremented primary key")
        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        void shouldReturnAUserFromTheAutoIncrementedPrimaryKey(DBConfig dbConfig, String dbName)
            throws Exception {
            setupTest(dbConfig, beforeEach);
            User user = DB.insert(insertUserQuery, User.class);
            assertEquals(username, user.getName());
            assertNotNull(user.getId());
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should return a NoIncrement with no auto incremented primary key")
        void shouldReturnANoIncrementWithNoAutoIncrementedPrimaryKey(DBConfig dbConfig, String dbName)
            throws Exception {
            setupTest(dbConfig, beforeEach);
            NoIncrement noIncrement = DB.insert(insertNoIncrementQuery, NoIncrement.class);
            assertEquals(message, noIncrement.getMessage());
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should throw exception if primary key already exists")
        void shouldThrowExceptionIfPrimaryKeyAlreadyExists(DBConfig dbConfig, String dbName)
            throws Exception {
            setupTest(dbConfig, beforeEach);
            DB.insert(insertNoIncrementQuery);
            assertThrows(DatabaseException.class, () -> DB.insert(insertNoIncrementQuery));
        }

    }

    @Nested
    @DisplayName("get")
    class Get {

        User user;

        private final VoidFunction beforeEach = () -> {
            insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
                .addParameter("name", username);
            insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message) VALUES (:message)")
                .addParameter("message", message);
            user = DB.insert(insertUserQuery, User.class);
            DB.insert(insertNoIncrementQuery);
        };

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should return a user")
        void shouldReturnAUser(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            User foundUser = DB.get(user.getId(), User.class);
            assertEquals(user.getId(), foundUser.getId());
            assertEquals(user.getName(), foundUser.getName());
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should throw exception if incorrect primary key")
        void shouldThrowExceptionIfIncorrectPrimaryKey(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            assertThrows(DatabaseException.class, () -> DB.get(10, User.class));
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should return a NoIncrement")
        void shouldReturnANoIncrement(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            NoIncrement noIncrement = DB.get(message, NoIncrement.class);
            assertEquals(message, noIncrement.getMessage());
        }

    }

    @Nested
    @DisplayName("get all")
    class GetAll {

        private final VoidFunction beforeEach = () -> {
            insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
                .addParameter("name", username);
            insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message) VALUES (:message)")
                .addParameter("message", message);
            DB.insert(insertUserQuery);
            DB.insert(insertUserQuery);
            DB.insert(insertUserQuery);
            DB.insert(insertNoIncrementQuery);
        };

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should return a list of users")
        void shouldReturnAListOfUsers(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            List<User> users = DB.getAll(User.class);
            assertEquals(3, users.size());
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should return a list of no increments")
        void shouldReturnAListOfNoIncrements(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            List<NoIncrement> noIncrements = DB.getAll(NoIncrement.class);
            assertEquals(1, noIncrements.size());
        }

    }

    @Nested
    @DisplayName("select")
    class Select {

        private final VoidFunction beforeEach = () -> {
            insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
                .addParameter("name", username);
            DB.insert(insertUserQuery);
            DB.insert(insertUserQuery);
            DB.insert(insertUserQuery);
        };

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should return a list of users with from name")
        void shouldReturnAListOfUsersWithFromName(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            SQLQuery query = new SQLQuery("SELECT * FROM users WHERE name = :name")
                .addParameter("name", username);
            List<User> users = DB.select(query, User.class);
            assertEquals(3, users.size());
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should throw exception if query fails")
        void shouldThrowExceptionIfQueryFails(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            SQLQuery query = new SQLQuery("SELECT * FROM fails");
            assertThrows(DatabaseException.class, () -> DB.select(query, NoIncrement.class));
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should return empty list of no increments when none exist")
        void shouldReturnEmptyListOfNoIncrementsWhenNoneExist(DBConfig dbConfig, String dbName)
            throws Exception {
            setupTest(dbConfig, beforeEach);
            SQLQuery query = new SQLQuery("SELECT * FROM no_increment");
            List<NoIncrement> noIncrements = DB.select(query, NoIncrement.class);
            assertNotNull(noIncrements);
            assertEquals(0, noIncrements.size());
        }

    }

    @Nested
    @DisplayName("update")
    class Update {

        SQLQuery updateUserQuery;
        SQLQuery updateNoIncrementQuery;

        String newName = "Updated Bob";
        String newMessage = "This is an updated message";

        User user;

        private final VoidFunction beforeEach = () -> {
            insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
                .addParameter("name", username);
            insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message) VALUES (:message)")
                .addParameter("message", message);
            user = DB.insert(insertUserQuery, User.class);
            DB.insert(insertUserQuery);
            DB.insert(insertUserQuery);
            DB.insert(insertNoIncrementQuery);
            updateUserQuery = new SQLQuery("UPDATE users SET name = :name WHERE id = :id")
                .addParameter("name", newName)
                .addParameter("id", user.getId());
            updateNoIncrementQuery = new SQLQuery(
                "UPDATE no_increment SET message = :newMessage WHERE message = :message")
                .addParameter("newMessage", newMessage)
                .addParameter("message", message);
        };

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should update a users name to a different value")
        void shouldUpdateAUsersNameToADifferentValue(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            User updatedUser = DB.update(updateUserQuery, User.class);
            assertEquals(user.getId(), updatedUser.getId());
            assertEquals(newName, updatedUser.getName());
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should not throw exception when updating user")
        void shouldNotThrowExceptionWhenUpdatingUser(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            assertDoesNotThrow(() -> DB.update(updateUserQuery));
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should not throw exception when updating no increment")
        void shouldNotThrowExceptionWhenUpdatingNoIncrement(DBConfig dbConfig, String dbName)
            throws Exception {
            setupTest(dbConfig, beforeEach);
            assertDoesNotThrow(() -> DB.update(updateNoIncrementQuery));
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should be able to update primary key of string")
        void shouldBeAbleToUpdatePrimaryKeyOfString(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            NoIncrement updatedNoIncrement = DB.update(updateNoIncrementQuery, NoIncrement.class);
            assertEquals(newMessage, updatedNoIncrement.getMessage());
            assertNotEquals(message, updatedNoIncrement.getMessage());
        }

    }

    @Nested
    @DisplayName("delete")
    class Delete {

        User user;

        private final VoidFunction beforeEach = () -> {
            insertUserQuery = new SQLQuery("INSERT INTO users (name) VALUES (:name)")
                .addParameter("name", username);
            insertNoIncrementQuery = new SQLQuery("INSERT INTO no_increment (message) VALUES (:message)")
                .addParameter("message", message);
            user = DB.insert(insertUserQuery, User.class);
            DB.insert(insertUserQuery);
            DB.insert(insertUserQuery);
            DB.insert(insertNoIncrementQuery);
        };

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should not throw when deleting user")
        void shouldNotThrowWhenDeletingUser(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            assertDoesNotThrow(() -> DB.delete(user.getId(), User.class));
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should not be able to find user when deleted")
        void shouldNotBeAbleToFindUserWhenDeleted(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            DB.delete(user.getId(), User.class);
            assertThrows(DatabaseException.class, () -> DB.get(user.getId(), User.class));
        }

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(DBConfigArgumentProvider.class)
        @DisplayName("should delete user with SQL query")
        void shouldDeleteUserWithSqlQuery(DBConfig dbConfig, String dbName) throws Exception {
            setupTest(dbConfig, beforeEach);
            SQLQuery deleteUserQuery = new SQLQuery("DELETE FROM users WHERE id = :id")
                .addParameter("id", user.getId());
            assertDoesNotThrow(() -> DB.delete(deleteUserQuery));
        }

    }


}