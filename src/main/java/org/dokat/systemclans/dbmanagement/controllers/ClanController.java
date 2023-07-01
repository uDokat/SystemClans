package org.dokat.systemclans.dbmanagement.controllers;

import org.dokat.systemclans.dbmanagement.data_models.Clan;

import java.sql.*;

public class ClanController {

    private static Connection connection = null;

    public ClanController(Connection connection) {
        ClanController.connection = connection;
    }

    public static void save(Clan clan){
        if (!isRecordExists(clan.getName())) {
            create(clan);
            return;
        }

        String sql = "UPDATE clans SET clan_name = ?, level = ?, balance = ?, amount_player = ?, welcome_message = ?, pvp_status = ?, kills = ?, reputation = ?, date_create = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, clan.getName());
            statement.setInt(2, clan.getLevel());
            statement.setLong(3, clan.getBalance());
            statement.setInt(4, clan.getAmountPlayers());
            statement.setString(5, clan.getWelcomeMessage());
            statement.setBoolean(6, clan.isPvpStatus());
            statement.setInt(7, clan.getKills());
            statement.setInt(8, clan.getReputation());
            statement.setString(9, clan.getDateCreate());
            statement.setInt(10, clan.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void create(Clan clan){
        if (!isClanNameNotExist(clan.getName())) return;

        String sql = "INSERT INTO clans (clan_name, level, balance, amount_player, welcome_message, pvp_status, kills, reputation, date_create) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, clan.getName());
            statement.setInt(2, clan.getLevel());
            statement.setLong(3, clan.getBalance());
            statement.setInt(4, clan.getAmountPlayers());
            statement.setString(5, clan.getWelcomeMessage());
            statement.setBoolean(6, clan.isPvpStatus());
            statement.setInt(7, clan.getKills());
            statement.setInt(8, clan.getReputation());
            statement.setString(9, clan.getDateCreate());
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()){
                clan.setId(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(Clan clan){
        PlayerController.delete(clan.getName());
        ClanHomeController.delete(clan.getId());

        String sql = "DELETE FROM clans WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clan.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Clan getClan(String clanName){
        Clan clan = null;
//        if (clan != null) return clan;

        String sql = "SELECT * FROM clans WHERE clan_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, clanName);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                clan = new Clan(clanName);
                clan.setId(resultSet.getInt("id"));
                clan.setLevel(resultSet.getInt("level"));
                clan.setBalance(resultSet.getInt("balance"));
                clan.setAmountPlayers(resultSet.getInt("amount_player"));
                clan.setWelcomeMessage(resultSet.getString("welcome_message"));
                clan.setPvpStatus(resultSet.getBoolean("pvp_status"));
                clan.setKills(resultSet.getInt("kills"));
                clan.setReputation(resultSet.getInt("reputation"));
                clan.setDateCreate(resultSet.getString("date_create"));
                //clan.setChanHome(new ClanHome);

//                DataController.addClan(clan);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return clan;
    }

    // возвращает тру если клан существует
    public static boolean isClanNameNotExist(String clanName){
        return getClan(clanName) == null;
    }

    private static boolean isRecordExists(String name) {
        boolean recordExists = false;

        String sql = "SELECT COUNT(*) FROM clans WHERE clan_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    recordExists = (count > 0);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return recordExists;
    }

}
