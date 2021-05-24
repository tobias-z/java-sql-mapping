package com.tobias_z.api.connection;

import com.tobias_z.DBConfig;
import com.tobias_z.DBSetting;
import java.util.HashMap;
import java.util.Map;

public class PostgresSQLTestDBConfig implements DBConfig {

    @Override
    public Map<DBSetting, String> getConfiguration() {
        Map<DBSetting, String> config = new HashMap<>();
        config.put(DBSetting.JDBC_DRIVER, "org.postgresql.Driver");
        config.put(DBSetting.USER, "postgres");
        config.put(DBSetting.PASSWORD, "ax2");
        config.put(DBSetting.URL, "jdbc:postgresql://localhost/chat_test");
        return config;
    }
}

// myReallyStrongPassword1234