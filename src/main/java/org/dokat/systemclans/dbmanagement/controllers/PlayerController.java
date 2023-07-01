package org.dokat.systemclans.dbmanagement.controllers;

import org.dokat.systemclans.dbmanagement.data_models.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerController {

    private static Connection connection = null;

    public PlayerController(Connection connection) {
        PlayerController.connection = connection;
    }

    public static void save(Player player){
        if (!isHaveClan(player.getUserName())){
            create(player);
            return;
        }

        String sql = "UPDATE players SET clan_name = ?, `group` = ?, kills = ?, balance = ?, date_add = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getClanName());
            statement.setInt(2, player.getGroup());
            statement.setInt(3, player.getKills());
            statement.setInt(4, (int) player.getContribute());
            statement.setString(5, player.getDateAdd());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void create(Player player){
        String sql = "INSERT INTO players (user_name, clan_name, `group`, kills, balance, date_add) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUserName());
            statement.setString(2, player.getClanName());
            statement.setInt(3, player.getGroup());
            statement.setInt(4, player.getKills());
            statement.setInt(5, (int) player.getContribute());
            statement.setString(6, player.getDateAdd());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(String clanName) {
        String sql = "DELETE FROM players WHERE clan_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, clanName);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(Player player){
        String sql = "DELETE FROM players WHERE user_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, player.getUserName());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Player getPlayer(String userName){
        Player player = DataController.getPlayer(userName);
//        if (player != null) return player;

        String sql = "SELECT * FROM players WHERE user_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                player = new Player(userName, resultSet.getString("clan_name"));
                player.setGroup(resultSet.getInt("group"));
                player.setKills(resultSet.getInt("kills"));
                player.setContribute(resultSet.getInt("balance"));
                player.setDateAdd(resultSet.getString("date_add"));

//                DataController.addPlayer(player);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return player;
    }

    public static boolean isHaveClan(String userName){
        boolean recordExists = false;

        String sql = "SELECT COUNT(*) FROM players WHERE user_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userName);
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