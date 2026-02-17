package com.indistudia.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbManager {
    private static Connection connection = null;

    private DbManager() {
    }

    public static Connection getConnection(DbConfig dbConfig) throws SQLException {
        if (connection == null) {
            connection = openConnection(dbConfig);
        }

        return connection;
    }

    public static Connection openConnection(DbConfig dbConfig) throws SQLException {
        return DriverManager.getConnection(dbConfig.jdbcUrl(), dbConfig.user(), dbConfig.password());
    }
}
