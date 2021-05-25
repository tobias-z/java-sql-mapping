package io.github.tobias_z.entities;

import io.github.tobias_z.annotations.Table;
import io.github.tobias_z.annotations.Column;
import io.github.tobias_z.annotations.PrimaryKey;

@Table(name = "no_increment")
public class NoIncrement {

    @PrimaryKey
    @Column(name = "message")
    private String message;

    @Column(name = "role")
    private Role role;

    public NoIncrement(String message, Role role) {
        this.message = message;
        this.role = role;
    }

    public NoIncrement() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "NoIncrement{" +
            "message='" + message + '\'' +
            '}';
    }
}
