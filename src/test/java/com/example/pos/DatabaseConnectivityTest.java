package com.example.pos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest
public class DatabaseConnectivityTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Connected to RDS: " + connection.getMetaData().getURL());
            assert (connection != null);
        } catch (Exception e) {
            e.printStackTrace();
            assert false : "Failed to connect to the database";
        }
    }
}
