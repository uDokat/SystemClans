package org.dokat.systemclans.dbmanagement.connections;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection {

    Connection getConnection() throws SQLException;
    void closeConnection(Connection connection) throws SQLException;
}
