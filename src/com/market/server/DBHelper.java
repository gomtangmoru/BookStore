package com.market.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    public static Connection getConnection() throws SQLException {
        String host = System.getenv().getOrDefault("SERVERDBHOST", "localhost");
        String port = System.getenv().getOrDefault("SERVERDBPORT", "3306");
        String dbName = System.getenv().getOrDefault("SERVERDBNAME", "bookstore");
        String user = System.getenv().getOrDefault("SERVERDBUSER", "root");
        String password = System.getenv().getOrDefault("SERVERDBPASSWORD", "1234");

        String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port, dbName);

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL Driver not found. Include mysql-connector-j in your classpath.");
        }

        return DriverManager.getConnection(url, user, password);
    }
}

