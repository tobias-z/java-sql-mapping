package io.github.tobias_z.api.connection;

import io.github.tobias_z.DBConfig;
import io.github.tobias_z.DBSetting;
import java.util.HashMap;
import java.util.Map;

public class SQLServerDBConfig implements DBConfig {

    @Override
    public Map<DBSetting, String> getConfiguration() {
        Map<DBSetting, String> config = new HashMap<>();
        config.put(DBSetting.JDBC_DRIVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        config.put(DBSetting.USER, "sa");
        config.put(DBSetting.PASSWORD, "thisIsSuperSecret1234321");
        config.put(DBSetting.URL, "jdbc:sqlserver://localhost;database=tempdb;");
        return config;
    }
}
