package com.tobias_z.entities;

import com.tobias_z.annotations.AutoIncremented;
import com.tobias_z.annotations.PrimaryKey;
import com.tobias_z.annotations.Column;
import com.tobias_z.annotations.Table;
import org.junit.jupiter.api.AfterAll;

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

    public User() {
    }

    public User(Integer id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
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

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
