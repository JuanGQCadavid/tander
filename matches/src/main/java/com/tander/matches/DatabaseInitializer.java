package com.tander.matches;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseInitializer {

    public static void initialize(String nameOfDatabase) {
        String url = "jdbc:postgresql://localhost:5432/";
        String username = "postgres";
        String password = "postgres";

        DataSource dataSource = new DriverManagerDataSource(url, username, password);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        try {
            jdbcTemplate.execute("CREATE DATABASE " + nameOfDatabase);
        } catch (Exception e) {
            System.err.println("Database already exists");
        }
    }
}
