package com.example.application.dbms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionTest {
    public static void run(String jdbcURL) throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcURL);
        connection.close();
    }
}
