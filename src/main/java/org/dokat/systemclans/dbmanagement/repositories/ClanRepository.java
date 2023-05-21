package org.dokat.systemclans.dbmanagement.repositories;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.cache.ClanStatusCache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClanRepository {

    private Connection connection;
    private ClanStatusCache cache;
    private String userName;

    public ClanRepository(Connection connection, String userName) {
        this.connection = connection;
        this.userName = userName;
        this.cache = new ClanStatusCache(connection, SystemClans.getCache());
    }

    //убрал синхр.

    public void createClan(String clanName){
        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO clans (clan_name, level, balance, amount_player) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, clanName);
            preparedStatement.setInt(2, 1);
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, 0);

            preparedStatement.executeUpdate();

            PlayerRepository repository = new PlayerRepository(connection);
            repository.savePlayer(userName, clanName, 2);
            cache.setClanStatus(userName, clanName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteClan(String userName){
        try (PreparedStatement preparedStatementPlayer = connection.prepareStatement("DELETE FROM players WHERE clan_name = ?");
             PreparedStatement preparedStatementClan = connection.prepareStatement("DELETE FROM clans WHERE clan_name = ? AND id = ?")) {

            String clanName =  cache.getClanName(userName);

            preparedStatementPlayer.setString(1, clanName);
            preparedStatementPlayer.executeUpdate();

            preparedStatementClan.setString(1, clanName);
            preparedStatementClan.setInt(2, getClanIdByName(clanName));
            preparedStatementClan.executeUpdate();

//            cache.deletePlayerFromCache(userName);
            // дописать метод что бы он брал всех пользователей из бд перед удалением и удалял из кэша
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

    public int getClanIdByName(String clanName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM clans WHERE clan_name = ?")) {
            preparedStatement.setString(1, clanName);

            int clanId = -1;

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                clanId = resultSet.getInt("id");
            }
            resultSet.close();

            return clanId;
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
        try (PreparedStatement preparedStatementClan = connection.prepareStatement("UPDATE clans SET clan_name = ? WHERE clan_name = ? AND id = ?");
             PreparedStatement preparedStatementPlayer = connection.prepareStatement("UPDATE players SET clan_name = ? WHERE clan_name = ? AND clan_id = ?")) {

            int clanId = getClanIdByName(clanName);

            preparedStatementClan.setString(1, newClanName);
            preparedStatementClan.setString(2, clanName);
            preparedStatementClan.setInt(3, clanId);
            preparedStatementClan.executeUpdate();

            preparedStatementPlayer.setString(1, newClanName);
            preparedStatementPlayer.setString(2, clanName);
            preparedStatementPlayer.setInt(3, clanId);
            preparedStatementPlayer.executeUpdate();
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
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET balance = ? WHERE clan_name = ?")) {
            preparedStatement.setInt(1, getClanBalance() - amount);
            preparedStatement.setString(2, clanName);
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
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET amount_player = ? WHERE clan_name = ?")) {
            preparedStatement.setInt(1, amountPlayer);
            preparedStatement.setString(2, clanName);
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
            }

            return new Location(Bukkit.getWorld(worldName), x, y, z);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLocationClanHome(String clanName, double x, double y, double z, String worldName){
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
}