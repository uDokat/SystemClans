package org.dokat.systemclans;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.dokat.systemclans.commands.AcceptCommand;
import org.dokat.systemclans.commands.ClanChat;
import org.dokat.systemclans.commands.ClanCommand;
import org.dokat.systemclans.dbmanagement.connections.DatabaseConnection;
import org.dokat.systemclans.dbmanagement.connections.JdbcDatabaseConnection;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.events.*;
import org.dokat.systemclans.management.ClanInviteManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public final class SystemClans extends JavaPlugin {

    private static SystemClans instance;

    private static Connection connection;
    private static ClanInviteManager clanInviteManager;
    // Возвращает лист игроков по клан нейму
    private static HashMap<String, ArrayList<Player>> playersInClan;
    // Возвращает статус пвп по клан нейму
    private static HashMap<String, Boolean> statusPvp;
    // Возвращает клан нейм игрока
    private static HashMap<Player, String> clanNameByPlayer;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Создание соединения с базой данных
        DatabaseConnection databaseConnection = new JdbcDatabaseConnection("jdbc:mysql://localhost:3306/clans", "root", "root");
        connection = databaseConnection.getConnection();

        try {
            // Создание таблиц кланов, игроков и домов кланов, если они не существуют
            createNewClansTable();
            createNewPlayersTable();
            createNewClanHomeTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        instance = this;
        playersInClan = new HashMap<>();
        statusPvp = new HashMap<>();
        clanNameByPlayer = new HashMap<>();
        clanInviteManager = new ClanInviteManager();

        //Заполняет хэшмапу пвп статусов по клан нейму
        ClanRepository repository = new ClanRepository(connection);
        repository.addStatusPvpInMap();

        // Регистрация команд, слушателей событий и менеджеров
        new ClanCommand();
        new ClanChat();
        new AcceptCommand();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerAttackListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerSwapListener(), this);
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

    /**
     * Создает новую таблицу `clans` в базе данных, если она не существует.
     * Таблица `clans` содержит информацию о кланах, их уровне, балансе, количестве игроков и т.д.
     * ...
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
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
                "reputation INT DEFAULT 0," +
                "date_create VARCHAR(50)" +
                ")");
    }

    /**
     * Создает новую таблицу `players` в базе данных, если она не существует.
     * Таблица `players` содержит информацию о игроках, их никнеймах, принадлежности к кланам и т.д.
     * ...
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    public void createNewPlayersTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS `players` (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "user_name VARCHAR(225)," +
                "clan_name VARCHAR(255)," +
                "`group` INT," +
                "killings INT DEFAULT 0," +
                "clan_id INT," +
                "date_add VARCHAR(50)," +
                "FOREIGN KEY (clan_id) REFERENCES clans(id)" +
                ")");
    }

    /**
     * Создает новую таблицу `clan_houses` в базе данных, если она не существует.
     * Таблица `clan_houses` содержит информацию о домах кланов, их координатах и т.д.
     * ...
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
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

    public static void setPlayersInClan(String clanName, ArrayList<Player> players){
        playersInClan.put(clanName, players);
    }
    public static HashMap<String, ArrayList<Player>> getPlayersInClan(){
        return playersInClan;
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
    public static HashMap<Player, String> getClanNameByPlayer() {
        return clanNameByPlayer;
    }
}