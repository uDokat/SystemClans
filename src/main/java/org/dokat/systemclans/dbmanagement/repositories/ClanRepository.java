package org.dokat.systemclans.dbmanagement.repositories;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class ClanRepository {

    private Connection connection;
    private String userName;
    private final ConfigManager config = new ConfigManager();
    private final int amountReputation = config.getClanSettings("amount_reputation_for_kills");
    private final int amountKills = config.getClanSettings("reputation_for_amount_kills");

    public ClanRepository(Connection connection, String userName) {
        this.connection = connection;
        this.userName = userName;
    }

    //убрал синхр.

    public void createClan(String clanName, Player player){
        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO clans (clan_name, level, balance, amount_player, date_create) VALUES (?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, clanName);
            preparedStatement.setInt(2, 1);
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, 0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
            LocalDateTime localDateTime = LocalDateTime.now();
            String time = localDateTime.format(formatter);
            preparedStatement.setString(5, time);

            preparedStatement.executeUpdate();

            SystemClans.setPlayersInClan(clanName, new ArrayList<>());

            PlayerRepository repository = new PlayerRepository(connection);
            repository.savePlayer(player, clanName, 2);

            SystemClans.getStatusPvp().put(clanName, true);
            SystemClans.getIsSameClan().put(player, clanName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteClan(String userName){
        try (PreparedStatement preparedStatementClan = connection.prepareStatement("DELETE FROM clans WHERE id = ?");
             PreparedStatement preparedStatementPlayer = connection.prepareStatement("DELETE FROM players WHERE clan_id = ?")) {

            String clanName = getClanName(userName);
            int clanId = getClanIdByName(clanName);

            if (getLocationClanHome(clanName) != null){
                PreparedStatement preparedStatementHome = connection.prepareStatement("DELETE FROM clan_houses WHERE clan_id = ?");
                preparedStatementHome.setInt(1, clanId);
                preparedStatementHome.executeUpdate();
            }

            preparedStatementPlayer.setInt(1, clanId);
            preparedStatementPlayer.executeUpdate();

            preparedStatementClan.setInt(1, clanId);
            preparedStatementClan.executeUpdate();


            ArrayList<Player> players = SystemClans.getPlayersInClan().get(clanName);
            for (Player player : players){
                SystemClans.getIsSameClan().remove(player);
            }

            SystemClans.getPlayersInClan().remove(clanName);
            SystemClans.getStatusPvp().remove(clanName);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isClanNameNotFound(String clanName){
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM clans WHERE clan_name = ?")) {
            statement.setString(1, clanName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count == 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public int getClanIdByName(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM clans WHERE clan_name = ?")) {
            preparedStatement.setString(1, clanName);

            int clanId = -1;

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                clanId = resultSet.getInt("id");
            }
            resultSet.close();

            return clanId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getClanName(String userName){
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT clan_name FROM players WHERE user_name = ?")) {
            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();

            String clanName = null;

            if (resultSet.next()) {
                clanName = resultSet.getString("clan_name");
            }

            resultSet.close();

            return clanName;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClanName(String clanName, String newClanName){
        try (PreparedStatement preparedStatementClan = connection.prepareStatement("UPDATE clans SET clan_name = ? WHERE id = ?");
             PreparedStatement preparedStatementPlayer = connection.prepareStatement("UPDATE players SET clan_name = ? WHERE clan_id = ?")) {

            int clanId = getClanIdByName(clanName);

            preparedStatementClan.setString(1, newClanName);
            preparedStatementClan.setInt(2, clanId);
            preparedStatementClan.executeUpdate();

            preparedStatementPlayer.setString(1, newClanName);
            preparedStatementPlayer.setInt(2, clanId);
            preparedStatementPlayer.executeUpdate();

            ArrayList<Player> players2 = SystemClans.getPlayersInClan().get(clanName);
            HashMap<Player, String> isSameClan = SystemClans.getIsSameClan();

            for (Player player : players2){
                isSameClan.remove(player);
                isSameClan.put(player, newClanName);
            }

            boolean statusPvp = SystemClans.getStatusPvp().remove(clanName);
            SystemClans.getStatusPvp().put(newClanName, statusPvp);

            ArrayList<Player> players = SystemClans.getPlayersInClan().get(clanName);
            SystemClans.getPlayersInClan().remove(clanName);
            SystemClans.getPlayersInClan().put(newClanName, players);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getClanLevel(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT level FROM clans WHERE clan_name = ?")) {
            preparedStatement.setString(1, clanName);

            int clanLevel = 0;

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                clanLevel = resultSet.getInt("level");
            }

            return clanLevel;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClanLevel(String clanName, int clanLevel){

    }

    public int getClanBalance(){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT balance FROM clans WHERE clan_name = ?")) {
            preparedStatement.setString(1, getClanName(userName));

            int balance = 0;

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                balance = resultSet.getInt("balance");
            }

            resultSet.close();
            return balance;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setClanBalance(String clanName, int amount){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET balance = ? WHERE id = ?")) {
            preparedStatement.setInt(1, getClanBalance() - amount);
            preparedStatement.setInt(2, getClanIdByName(clanName));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getAmountPlayer(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT amount_player FROM clans WHERE clan_name = ?")) {
            preparedStatement.setString(1, clanName);

            int amountPlayer = 0;

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                amountPlayer = resultSet.getInt("amount_player");
            }

            resultSet.close();
            return  amountPlayer;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAmountPlayer(String clanName, int amountPlayer){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET amount_player = ? WHERE id = ?")) {
            preparedStatement.setInt(1, amountPlayer);
            preparedStatement.setInt(2, getClanIdByName(clanName));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Location getLocationClanHome(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT x, y, z, world_name FROM clan_houses WHERE clan_id = ?")) {
            int clanId = getClanIdByName(clanName);
            preparedStatement.setDouble(1, clanId);

            double x = 0;
            double y = 0;
            double z = 0;
            String worldName = null;

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                x = resultSet.getDouble("x");
                y = resultSet.getDouble("y");
                z = resultSet.getDouble("z");
                worldName = resultSet.getString("world_name");

                return new Location(Bukkit.getWorld(worldName), x, y, z);
            }else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createLocationClanHome(String clanName, double x, double y, double z, String worldName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO clan_houses (x, y, z, world_name, clan_id) VALUES (?, ?, ? ,?, ?)")) {

            int clanId = getClanIdByName(clanName);

            preparedStatement.setDouble(1, x);
            preparedStatement.setDouble(2, y);
            preparedStatement.setDouble(3, z);
            preparedStatement.setString(4, worldName);
            preparedStatement.setInt(5, clanId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLocationClanHome(String clanName, double x, double y, double z, String worldName){
        if (getLocationClanHome(clanName) == null){
            createLocationClanHome(clanName, x, y, z, worldName);
        }else {
            try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clan_houses SET x = ?, y = ?, z = ?, world_name = ? WHERE clan_id = ?")) {

                int clanId = getClanIdByName(clanName);

                preparedStatement.setDouble(1, x);
                preparedStatement.setDouble(2, y);
                preparedStatement.setDouble(3, z);
                preparedStatement.setString(4, worldName);
                preparedStatement.setInt(5, clanId);
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getWelcomeMessage(String clanName) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT welcome_massage FROM clans WHERE id = ?")) {
            preparedStatement.setInt(1, getClanIdByName(clanName));

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("welcome_massage");
            }else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setWelcomeMessage(String clanName, String welcomeMessage){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET welcome_massage = ? WHERE id = ?")) {

            int clanId = getClanIdByName(clanName);

            preparedStatement.setString(1, welcomeMessage);
            preparedStatement.setInt(2, clanId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getStatusPvp(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT pvp FROM clans WHERE id = ?")) {
            preparedStatement.setInt(1, getClanIdByName(clanName));

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getBoolean("pvp");
            }else {
                return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStatusPvp(String clanName, boolean statusPvp){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET pvp = ? WHERE id = ?")) {
            preparedStatement.setBoolean(1, statusPvp);
            preparedStatement.setInt(2, getClanIdByName(clanName));
            preparedStatement.executeUpdate();

            SystemClans.getStatusPvp().remove(clanName);
            SystemClans.getStatusPvp().put(clanName, statusPvp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addStatusPvpInMap(){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT pvp, clan_name FROM clans")) {
            HashMap<String, Boolean> statusPvp = SystemClans.getStatusPvp();

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                statusPvp.put(resultSet.getString("clan_name"), resultSet.getBoolean("pvp"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getAmountKillings(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT killings FROM clans WHERE id = ?")) {
            preparedStatement.setInt(1, getClanIdByName(clanName));

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

    public void addAmountKillings(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET killings = ? WHERE id = ?")) {

            preparedStatement.setInt(1,  getAmountKillings(clanName) + 1);
            preparedStatement.setInt(2, getClanIdByName(clanName));
            preparedStatement.executeUpdate();

            if (getAmountKillings(clanName) % amountKills == 0){
                setReputation(clanName, getReputation(clanName) + amountReputation);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getReputation(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT reputation FROM clans WHERE id = ?")) {
            preparedStatement.setInt(1, getClanIdByName(clanName));

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("reputation");
            }else {
                resultSet.close();
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setReputation(String clanName, int reputation){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET reputation = ? WHERE id = ?")) {
            preparedStatement.setInt(1, reputation);
            preparedStatement.setInt(2, getClanIdByName(clanName));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDateCreate(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT date_create FROM clans WHERE id = ?")) {
            preparedStatement.setInt(1, getClanIdByName(clanName));

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("date_create");
            }else {
                return "";
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}