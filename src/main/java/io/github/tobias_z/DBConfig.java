package io.github.tobias_z;

import java.util.Map;

public interface DBConfig {

    /**
     * @return The database configuration. You can use multiple different DBConfig's such as a
     * DBMySQLTestConfig and a DBMySQLConfig
     */
    Map<DBSetting, String> getConfiguration();

}
