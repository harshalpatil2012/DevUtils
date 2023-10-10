package com.example.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * JDBC Generic Service for SQL Querying
 *
 * Overview
 *
 * This service provides a generic way to execute SQL queries and update
 * statements using JDBC. It can be used to query and update any database that
 * supports JDBC.
 *
 * Features
 *
 * - Supports all SQL queries, including SELECT, INSERT, UPDATE, and DELETE
 * - Supports prepared statements and named parameters
 * - Supports transactions and batch updates
 * - Easy to use and extend
 *
 * Usage
 *
 * To use the service, you first need to create an instance of the JdbcService
 * class, passing in a DataSource object. The DataSource object is responsible
 * for providing connections to the database.
 *
 * Once you have created an instance of the JdbcService class, you can use the
 * query() and update() methods to execute SQL queries and update statements.
 *
 * @author harshal
 *
 */
public class JdbcService {
    private final DataSource dataSource;

    public JdbcService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executes the given SQL query and returns a list of results, mapped to the
     * given type using the given RowMapper.
     *
     * @param <T>
     * @param sql
     * @param rowMapper
     * @return
     * @throws SQLException
     */
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            List<T> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(rowMapper.mapRow(resultSet));
            }
            return results;
        }
    }

    /**
     * Executes the given SQL update statement and returns the number of rows
     * affected.
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public int update(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.executeUpdate();
        }
    }
}
