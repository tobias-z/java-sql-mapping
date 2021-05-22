package com.tobias_z.entities;

import com.tobias_z.annotations.Column;
import com.tobias_z.annotations.PrimaryKey;
import com.tobias_z.annotations.Table;

@Table(name = "no_increment")
public class NoIncrement {

    @PrimaryKey
    @Column(name = "message")
    private String message;

    public NoIncrement(String message) {
        this.message = message;
    }

    public NoIncrement() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "NoIncrement{" +
            "message='" + message + '\'' +
            '}';
    }
}
