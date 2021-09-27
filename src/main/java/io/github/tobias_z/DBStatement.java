package io.github.tobias_z;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface DBStatement {
    PreparedStatement getPreparedStatement(Connection connection) throws SQLException;
}
