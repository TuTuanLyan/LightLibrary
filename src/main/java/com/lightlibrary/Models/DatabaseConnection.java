package com.lightlibrary.Models;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection databaseLink;

    /**
     * Get Connection to database.
     * @return a Connection data is link to connect with database.
     */
    public static Connection getConnection() {
        String databaseName = "LibraryDBNew";
        String databaseUser = "root";
        String databasePassword = "50022111";

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