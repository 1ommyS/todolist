package com.indistudia.config;

public record DbConfig(
        String jdbcUrl,
        String user,
        String password
) {
}
