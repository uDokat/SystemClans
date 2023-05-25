package org.dokat.systemclans;

import org.bukkit.plugin.java.JavaPlugin;
import org.dokat.systemclans.commands.AcceptCommand;
import org.dokat.systemclans.commands.ClanCommand;
import org.dokat.systemclans.dbmanagement.connections.DatabaseConnection;
import org.dokat.systemclans.dbmanagement.connections.JdbcDatabaseConnection;
import org.dokat.systemclans.tasks.ClanInviteManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SystemClans extends JavaPlugin {

    private static SystemClans instance;

    private static Connection connection;
    private DatabaseConnection databaseConnection;
    private static ClanInviteManager clanInviteManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        databaseConnection = new JdbcDatabaseConnection("jdbc:mysql://localhost:3306/clans","root", "root");
        try {
            connection = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        clanInviteManager = new ClanInviteManager();

        new ClanCommand();
        new AcceptCommand();
    }

    @Override
    public void onDisable() {
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void createNewClansTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS `clans` (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "clan_name VARCHAR(225)," +
                "level INT," +
                "balance INT," +
                "amount_player INT," +
                "welcome_massage VARCHAR(255)," +
                "pvp TINYINT" +
                ")");
    }

    public void createNewPlayersTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS `players` (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "user_name VARCHAR(225)," +
                "clan_name VARCHAR(255)," +
                "`group` INT," +
                "clan_id INT," +
                "FOREIGN KEY (clan_id) REFERENCES clans(id)" +
                ")");
    }

    public void createNewClanHomeTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS `clan_houses` (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "x DOUBLE," +
                "y DOUBLE," +
                "z DOUBLE," +
                "world_name VARCHAR(50)," +
                "clan_id INT," +
                "FOREIGN KEY (clan_id) REFERENCES clans(id)" +
                ")");
    }

    public static ClanInviteManager getClanInviteManager(){
        return clanInviteManager;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static SystemClans getInstance() {
        return instance;
    }
}