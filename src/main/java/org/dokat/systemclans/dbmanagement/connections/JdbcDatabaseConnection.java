package org.dokat.systemclans.dbmanagement.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcDatabaseConnection implements DatabaseConnection{

    private String url;
    private String username;
    private String password;
    private Connection connection;

    public JdbcDatabaseConnection(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()){
            connection.close();
        }
    }
}
