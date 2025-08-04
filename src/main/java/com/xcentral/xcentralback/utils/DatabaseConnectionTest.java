package com.xcentral.xcentralback.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseConnectionTest(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Database Connection Test ===");
        try {
            Connection connection = dataSource.getConnection();
            System.out.println("✅ Database connection successful!");
            System.out.println("Database URL: " + connection.getMetaData().getURL());
            System.out.println("Database Product: " + connection.getMetaData().getDatabaseProductName());
            connection.close();
        } catch (Exception e) {
            System.out.println("❌ Database connection failed!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== End Database Connection Test ===");
    }
}
