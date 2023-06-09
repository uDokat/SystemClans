package org.dokat.systemclans;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.dokat.systemclans.commands.*;
import org.dokat.systemclans.dbmanagement.connection.HConfig;
import org.dokat.systemclans.dbmanagement.controllers.ClanController;
import org.dokat.systemclans.dbmanagement.controllers.ClanHomeController;
import org.dokat.systemclans.dbmanagement.controllers.DataController;
import org.dokat.systemclans.dbmanagement.controllers.PlayerController;
import org.dokat.systemclans.dbmanagement.tasks.SaveDataTask;
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
        instance = this;

        // Создание соединения с базой данных
        HConfig config = new HConfig("jdbc:mysql://mysql-133616-0.cloudclusters.net:16340/clans", "admin", "ixuqv1Cr");
//        HConfig config = new HConfig("jdbc:mysql://localhost:3306/clans", "root", "root");
        connection = config.getConnection();

        createTables();

        playersInClan = new HashMap<>();
        statusPvp = new HashMap<>();
        clanNameByPlayer = new HashMap<>();
        clanInviteManager = new ClanInviteManager();

        new ClanController(connection);
        new PlayerController(connection);
        new ClanHomeController(connection);

        new SaveDataTask();

        // Регистрация команд, слушателей событий и менеджеров
        new ClanCommand();
        new ClanChat();
        new AcceptCommand();
        new SaveDataCommand();

        //Сделать автоматическую регистрацию
//        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
//        getServer().getPluginManager().registerEvents(new PlayerAttackListener(), this);
//        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
////        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
//        new InventoryClickListener();
//        getServer().getPluginManager().registerEvents(new PlayerSwapListener(), this);
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

    public void createTables(){
        try {
            // Создание таблиц кланов, игроков и домов кланов, если они не существуют
            createNewClansTable();
            createNewPlayersTable();
            createNewClanHomeTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                "welcome_message VARCHAR(255)," +
                "pvp_status TINYINT," +
                "kills INT DEFAULT 0," +
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
                "kills INT DEFAULT 0," +
                "balance INT DEFAULT 0," +
                "date_add VARCHAR(50)," +
                "clan_id INT," +
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