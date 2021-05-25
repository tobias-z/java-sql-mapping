package io.github.tobias_z.entities;

import io.github.tobias_z.annotations.AutoIncremented;
import io.github.tobias_z.annotations.PrimaryKey;
import io.github.tobias_z.annotations.Column;
import io.github.tobias_z.annotations.Table;

@Table(name = "users")
public class User {


    @AutoIncremented
    @PrimaryKey
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "active")
    private boolean active;

    @Column(name = "role")
    private Role role;

    public User() {
    }

    public User(Integer id, String name, boolean active, Role role) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
