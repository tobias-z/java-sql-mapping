package com.tobias_z.api;

import com.tobias_z.exceptions.DatabaseException;

@FunctionalInterface
public interface VoidFunction {
    void apply() throws Exception;
}
