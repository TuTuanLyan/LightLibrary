package com.lightlibrary.Models;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection databaseLink;

    /**
     * Get Connection to database.
     * @return a Connection data is link to connect with database.
     */
    public Connection getConnection() {
        String databaseName = "LoginData";
        String databaseUser = "root";
        String databasePassword = "#1Tutuan313";

        String databaseUrl = "jdbc:mysql://localhost:3306/" + databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return databaseLink;
    }
}
