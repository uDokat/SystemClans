package org.dokat.systemclans.dbmanagement.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HConfig {

    private final HikariConfig config;

    public HConfig(String url, String name, String password){
        config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(name);
        config.setPassword(password);
    }

    public HikariConfig getConfig(){
        return config;
    }

    public Connection getConnection(){
        HikariDataSource source = new HikariDataSource(getConfig());
        try {
            return source.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
