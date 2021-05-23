package com.tobias_z.api.connection;

import com.tobias_z.DBConfig;
import com.tobias_z.DBSetting;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import org.apache.ibatis.jdbc.ScriptRunner;

public class SetupIntegrationTests {

    public void runTestDatabaseMigration(DBConfig dbConfig, String migrateFile) {
        Map<DBSetting, String> settings = dbConfig.getConfiguration();
        String TEST_DB_URL = settings.get(DBSetting.URL);
        String USER = settings.get(DBSetting.USER);
        String PASS = settings.getOrDefault(DBSetting.PASSWORD, null);

        InputStream stream = SetupIntegrationTests.class.getClassLoader().getResourceAsStream(
            migrateFile);
        if (stream == null) {
            System.out.println("Migration file, does not exist: ");
            throw new RuntimeException(migrateFile);
        }
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, USER, PASS)) {
            conn.setAutoCommit(false);
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setStopOnError(true);
            runner.runScript(new BufferedReader(new InputStreamReader(stream)));
            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Done running migration");
    }

}
