package io.github.tobias_z.api.connection;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.DBSetting;
import java.util.HashMap;
import java.util.Map;

public class MySQLTestDBConfig implements DBConfig {

    @Override
    public Map<DBSetting, String> getConfiguration() {
        Map<DBSetting, String> config = new HashMap<>();
        config.put(DBSetting.JDBC_DRIVER, "com.mysql.cj.jdbc.Driver");
        config.put(DBSetting.USER, "dev");
        config.put(DBSetting.PASSWORD, "SECRET");
        config.put(DBSetting.URL, "jdbc:mysql://localhost/chat_test");
        return config;
    }
}
