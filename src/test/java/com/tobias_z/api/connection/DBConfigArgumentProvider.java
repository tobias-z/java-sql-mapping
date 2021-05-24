package com.tobias_z.api.connection;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class DBConfigArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
            Arguments.of(new MySQLTestDBConfig(), "MySQL", "mysql-init.sql"),
            Arguments.of(new PostgresSQLTestDBConfig(), "PostgresSQL", "postgres-init.sql")
        );
    }
}
