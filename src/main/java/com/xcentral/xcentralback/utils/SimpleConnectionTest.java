package com.xcentral.xcentralback.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleConnectionTest {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://aws-0-us-east-2.pooler.supabase.com:5432/postgres?sslmode=require&connectTimeout=60&socketTimeout=60";
        String username = "postgres.dzjxerwuoieoqxvpphky";
        String password = "RnHPJz2AJZFCnjc8";

        System.out.println("=== Simple PostgreSQL Connection Test ===");
        System.out.println("Attempting to connect to: " + url);

        try {
            // Load the PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ PostgreSQL driver loaded successfully");

            // Attempt connection
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Database connection successful!");

            // Get database info
            System.out.println("Database URL: " + connection.getMetaData().getURL());
            System.out.println("Database Product: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("Database Version: " + connection.getMetaData().getDatabaseProductVersion());

            // Close connection
            connection.close();
            System.out.println("✅ Connection closed successfully");

        } catch (ClassNotFoundException e) {
            System.out.println("❌ PostgreSQL driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed!");
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Unexpected error!");
            e.printStackTrace();
        }

        System.out.println("=== End Connection Test ===");
    }
}
