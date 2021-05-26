package io.github.tobias_z.api;

import java.util.HashMap;
import java.util.Map;

public class SQLQuery {

    private String sql;
    private final Map<String, String> parameters;

    /**
     * @param sql Your SQL string. Example of param ':name'. The key here is the use of a :
     */
    public SQLQuery(String sql) {
        this.sql = sql;
        this.parameters = new HashMap<>();
    }

    /**
     * @param parameterName Parameter name corresponding to the param set in your query. So if you have a
     *                      param ':name' this would be 'name'
     * @param value         Whatever value you want this parameter to be.
     * @return The currently constructed SQLQuery
     */
    public <T> SQLQuery addParameter(String parameterName, T value) {
        parameters.put(parameterName, String.valueOf(value));
        return this;
    }

    Map<String, String> getParameters() {
        return parameters;
    }

    String getSql() {
        return sql;
    }

    void setSql(String sql) {
        this.sql = sql;
    }
}
