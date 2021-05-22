package com.tobias_z.domain;

import com.tobias_z.annotations.AutoIncremented;
import com.tobias_z.annotations.PrimaryKey;
import com.tobias_z.annotations.Column;
import com.tobias_z.annotations.Table;

@Table(name = "users")
public class User {

    @AutoIncremented
    @PrimaryKey
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    public User() {
    }

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
