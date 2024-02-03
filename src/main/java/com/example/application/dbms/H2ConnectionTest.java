package com.example.application.dbms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionTest {
    public static void run(String jdbcURL, String driver) {
        try {
            Connection connection = DriverManager.getConnection(jdbcURL);

            System.out.println("Connected to H2 in-memory database.");

            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
