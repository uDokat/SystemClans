package org.dokat.systemclans.dbmanagement.controllers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.dokat.systemclans.dbmanagement.data_models.ClanHome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClanHomeController {

    private static Connection connection;

    public ClanHomeController(Connection connection){
        ClanHomeController.connection = connection;
    }

    public static void save(ClanHome home, int id){
        if (!isClanHomeExist(id)){
            create(home, id);
            return;
        }

        String sql = "UPDATE clan_houses SET x = ?, y = ?, z = ?, world_name = ? WHERE clan_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, home.getX());
            statement.setDouble(2, home.getY());
            statement.setDouble(3, home.getY());
            statement.setString(4, home.getWoldName());
            statement.setInt(5, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void create(ClanHome home, int id){
        String sql = "INSERT INTO clan_houses (x, y, z, world_name, clan_id) VALUES (?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, home.getX());
            statement.setDouble(2, home.getY());
            statement.setDouble(3, home.getY());
            statement.setString(4, home.getWoldName());
            statement.setInt(5, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(int id){
        String sql = "DELETE FROM clan_houses WHERE clan_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Location getClanHome(int id){
        Location location = null;

        String sql = "SELECT * FROM clan_houses WHERE clan_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                int z = resultSet.getInt("z");
                String worldName = resultSet.getString("world_name");

                location = new Location(Bukkit.getWorld(worldName), x, y, z);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
        return location;
    }

    public static boolean isClanHomeExist(int id){
        boolean result = false;

        String sql = "SELECT COUNT(*) FROM clan_houses WHERE clan_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                result = (count > 0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
