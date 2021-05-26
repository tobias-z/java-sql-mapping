package io.github.tobias_z.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * REQUIRED
 * Set on your primary key
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
}
