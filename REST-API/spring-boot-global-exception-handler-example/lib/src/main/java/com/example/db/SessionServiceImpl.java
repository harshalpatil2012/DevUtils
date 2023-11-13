package com.example.db;

import java.util.List;

public class SessionServiceImpl implements SessionService {
    private final JdbcService jdbcService;

    public SessionServiceImpl(JdbcService jdbcService) {
        this.jdbcService = jdbcService;
    }

    @Override
    public List<Session> getAllSessions() {
        String sql = "SELECT * FROM session"; // Replace with actual SQL query.

        try {
            return jdbcService.query(sql, resultSet -> {
                Session session = new Session();
                session.setId(resultSet.getInt("id"));
                session.setName(resultSet.getString("name"));
                session.setStartTime(resultSet.getTimestamp("start_time").toLocalDateTime());
                session.setEndTime(resultSet.getTimestamp("end_time").toLocalDateTime());
                // Map other session properties as needed.
                return session;
            });
        } catch (SQLException e) {
            // Handle exceptions or log errors.
            e.printStackTrace();
            return null; // Return an appropriate response or handle the error as needed.
        }
    }
}
