package org.dokat.systemclans;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.dokat.systemclans.commands.AcceptCommand;
import org.dokat.systemclans.commands.ClanChat;
import org.dokat.systemclans.commands.ClanCommand;
import org.dokat.systemclans.commands.TestCommand;
import org.dokat.systemclans.dbmanagement.connections.DatabaseConnection;
import org.dokat.systemclans.dbmanagement.connections.JdbcDatabaseConnection;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.events.PlayerAttackListener;
import org.dokat.systemclans.events.PlayerDeathListener;
import org.dokat.systemclans.events.PlayerJoinListener;
import org.dokat.systemclans.tasks.ClanInviteManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SystemClans extends JavaPlugin {

    private static SystemClans instance;

    private static Connection connection;
    private static ClanInviteManager clanInviteManager;
    private static HashMap<String, ArrayList<Player>> playersInClan;
    private static HashMap<String, Boolean> statusPvp;
    private static HashMap<Player, String> isSameClan;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        DatabaseConnection databaseConnection = new JdbcDatabaseConnection("jdbc:mysql://localhost:3306/clans", "root", "root");
        try {
            connection = databaseConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        instance = this;
        playersInClan = new HashMap<>();
        statusPvp = new HashMap<>();
        isSameClan = new HashMap<>();
        clanInviteManager = new ClanInviteManager();

        ClanRepository repository = new ClanRepository(connection, "");
        repository.addStatusPvpInMap();

        new ClanCommand();
        new ClanChat();
        new AcceptCommand();
        new TestCommand();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerAttackListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
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
                "pvp TINYINT," +
                "killings INT DEFAULT 0," +
                "reputation INT DEFAULT 0" +
                ")");
    }

    public void createNewPlayersTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS `players` (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "user_name VARCHAR(225)," +
                "clan_name VARCHAR(255)," +
                "`group` INT," +
                "killings INT DEFAULT 0," +
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

    public static HashMap<String, ArrayList<Player>> getPlayersInClan() {
        return playersInClan;
    }

    public static void setPlayersInClan(String clanName, ArrayList<Player> players){
        playersInClan.put(clanName, players);
    }

    public static HashMap<String, Boolean> getStatusPvp() {
        return statusPvp;
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

    public static HashMap<Player, String> getIsSameClan() {
        return isSameClan;
    }
}