package com.github.tobias_z;

import java.util.HashMap;
import java.util.Map;

public class SQLQuery {

    private String sql;
    private final Map<String, String> parameters;

    public SQLQuery(String sql) {
        this.sql = sql;
        this.parameters = new HashMap<>();
    }

    public <T> SQLQuery addParameter(String parameterName, T value) {
        parameters.put(parameterName, String.valueOf(value));
        return this;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
