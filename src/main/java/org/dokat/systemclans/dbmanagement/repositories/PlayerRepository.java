package org.dokat.systemclans.dbmanagement.repositories;

import org.bukkit.entity.Player;
import org.dokat.systemclans.SystemClans;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerRepository {

    private Connection connection;

    public PlayerRepository(Connection connection) {
        this.connection = connection;
    }

    //убрал синхр

    public void savePlayer(Player player, String clanName, int group){
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (user_name, clan_name, `group`, clan_id) VALUES (?, ?, ?, ?)")) {

            ClanRepository repository = new ClanRepository(connection, "");
            int clanID = repository.getClanIdByName(clanName);

            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, clanName);
            preparedStatement.setInt(3, group);
            preparedStatement.setInt(4, clanID);
            preparedStatement.executeUpdate();

            repository.setAmountPlayer(clanName, repository.getAmountPlayer(clanName) + 1);
            SystemClans.getPlayersInClan().get(clanName).add(player);

            SystemClans.getIsSameClan().put(player, clanName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deletePlayer(String clanName, Player deletePlayer){
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM players WHERE user_name = ?")) {
            preparedStatement.setString(1, deletePlayer.getName());
            preparedStatement.executeUpdate();

            ClanRepository repository = new ClanRepository(connection, "");

            repository.setAmountPlayer(clanName, repository.getAmountPlayer(clanName) - 1);
            ArrayList<Player> players = SystemClans.getPlayersInClan().get(clanName);
            players.remove(deletePlayer);

            SystemClans.getIsSameClan().remove(deletePlayer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPlayerGroup(String userName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT `group` FROM players WHERE user_name = ?")) {
            preparedStatement.setString(1, userName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    public void setPlayerGroup(String userName, int group){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET `group` = ? WHERE user_name = ? AND clan_id = ?")) {

            ClanRepository repository = new ClanRepository(connection, userName);
            int claId =  repository.getClanIdByName(repository.getClanName(userName));

            preparedStatement.setInt(1, group);
            preparedStatement.setString(2, userName);
            preparedStatement.setInt(3, claId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllPlayersForClanName(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT user_name FROM players WHERE clan_name = ?")) {
            preparedStatement.setString(1, clanName);

            List<String> list = new ArrayList<>();

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                list.add(resultSet.getString("user_name"));
            }

            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String groupToString(int group){
        if (group == 0){
            return "Игрок";
        } else if (group == 1) {
            return "Доверенный";
        }else {
            return "OWN";
        }
    }

    public int getAmountKills(String userName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT killings FROM players WHERE user_name = ?")) {
            preparedStatement.setString(1, userName);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("killings");
            }else {
                resultSet.close();
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addAmountKills(String userName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET killings = ? WHERE user_name = ?")) {
            preparedStatement.setInt(1, getAmountKills(userName) + 1);
            preparedStatement.setString(2, userName);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
