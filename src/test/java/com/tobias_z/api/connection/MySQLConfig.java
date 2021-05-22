package com.tobias_z.api.connection;

import com.tobias_z.DBConfig;
import com.tobias_z.DBSetting;
import java.util.HashMap;
import java.util.Map;

public class MySQLConfig implements DBConfig {

    @Override
    public Map<DBSetting, String> getConfiguration() {
        Map<DBSetting, String> config = new HashMap<>();
        config.put(DBSetting.JDBC_DRIVER, "com.mysql.cj.jdbc.Driver");
        config.put(DBSetting.USER, "dev");
        config.put(DBSetting.PASSWORD, "ax2");
        config.put(DBSetting.URL, "jdbc:mysql://localhost:3306/chat_test");
        return config;
    }
}
