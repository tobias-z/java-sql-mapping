package com.tobias_z.api;

import com.tobias_z.DBConfig;
import java.util.HashMap;
import java.util.Map;

public class Config implements DBConfig {

    @Override
    public Map<String, String> getConfiguration() {
        Map<String, String> config = new HashMap<>();
        config.put("jdbc-driver", "com.mysql.cj.jdbc.Driver");
        config.put("user", "dev");
        config.put("password", "ax2");
        config.put("url", "jdbc:mysql://localhost:3306/chat");
        return config;
    }

}
