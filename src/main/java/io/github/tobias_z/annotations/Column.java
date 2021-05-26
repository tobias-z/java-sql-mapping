package io.github.tobias_z.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * REQUIRED
 * Set on each column corresponding to a column in your database
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String name();

}

