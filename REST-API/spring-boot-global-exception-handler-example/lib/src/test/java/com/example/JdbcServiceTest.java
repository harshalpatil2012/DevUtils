package com.example;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class JdbcServiceTest {

    @Autowired
    private JdbcService jdbcService;

    @MockBean
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Test
    public void testQuery_PositiveCase() throws SQLException {
        // Mocking
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Define a row mapper for testing
        RowMapper<String> rowMapper = resultSet1 -> resultSet1.getString("columnName");

        // Mock the resultSet
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("columnName")).thenReturn("TestValue");

        List<String> result = jdbcService.query("SELECT columnName FROM tableName", rowMapper);

        // Verification
        verify(connection, times(1)).close();
        verify(preparedStatement, times(1)).close();
        verify(resultSet, times(1)).close();

        assertEquals(1, result.size());
        assertEquals("TestValue", result.get(0));
    }

    @Test
    public void testQuery_NegativeCase() throws SQLException {
        // Mocking
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Test Error"));

        // Define a row mapper for testing
        RowMapper<String> rowMapper = resultSet1 -> resultSet1.getString("columnName");

        // The actual query call is expected to throw an SQLException.
        assertThrows(SQLException.class, () -> jdbcService.query("SELECT columnName FROM tableName", rowMapper));
    }

    @Test
    public void testUpdate_PositiveCase() throws SQLException {
        // Mocking
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(prepared statement.executeUpdate()).thenReturn(1);

        int result = jdbcService.update("UPDATE tableName SET columnName = 'NewValue'");

        // Verification
        verify(connection, times(1)).close();
        verify(prepared statement, times(1)).close();

        assertEquals(1, result);
    }

    @Test
    public void testUpdate_NegativeCase() throws SQLException {
        // Mocking
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(prepared statement.executeUpdate()).thenThrow(new SQLException("Test Error"));

        // The actual update call is expected to throw an SQLException.
        assertThrows(SQLException.class, () -> jdbcService.update("UPDATE tableName SET columnName = 'NewValue'"));
    }
}
