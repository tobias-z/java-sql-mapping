package io.github.tobias_z.utils;

import io.github.tobias_z.DBStatement;
import io.github.tobias_z.entities.Role;

import java.sql.PreparedStatement;
import java.sql.Statement;

public class DBStatements {

    public static DBStatement insertUser(String username, boolean active) {
        return connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (name, active, role) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, username);
            ps.setBoolean(2, active);
            ps.setString(3, Role.ADMIN.name());
            return ps;
        };
    }

    public static DBStatement insertNoIncrement(String message) {
        return connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO no_increment (message, role) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, message);
            ps.setString(2, Role.EMPLOYEE.name());
            return ps;
        };
    }

}
