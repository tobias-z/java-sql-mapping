package com.tobias_z.api.connection;

import com.tobias_z.DBConfig;
import com.tobias_z.DBSetting;
import java.util.HashMap;
import java.util.Map;

public class SQLServerDBConfig implements DBConfig {

    @Override
    public Map<DBSetting, String> getConfiguration() {
        Map<DBSetting, String> config = new HashMap<>();
        config.put(DBSetting.JDBC_DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        config.put(DBSetting.USER, "sa");
        config.put(DBSetting.PASSWORD, "myReallyStrongPassword1234");
        config.put(DBSetting.URL, "jdbc:sqlserver://localhost;database=chat_test;");
        return config;
    }
}
