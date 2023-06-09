package org.dokat.systemclans.dbmanagement.repositories;

import org.bukkit.entity.Player;
import org.dokat.systemclans.SystemClans;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для работы с данными игроков.
 */
public class PlayerRepository {

    private final Connection connection;

    /**
     * Конструктор класса PlayerRepository.
     *
     * @param connection Объект Connection для подключения к базе данных
     */
    public PlayerRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Сохраняет игрока в базе данных.
     *
     * @param player    Объект Player, представляющий игрока
     * @param clanName  Название клана, к которому принадлежит игрок
     * @param group     Группа игрока
     */
    public void savePlayer(Player player, String clanName, int group){
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (user_name, clan_name, `group`, date_add, clan_id) VALUES (?, ?, ?, ?, ?)")) {
            ClanRepository repository = new ClanRepository(connection);
            int clanID = repository.getClanIdByName(clanName);

            //Время добавления игрока в клан
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
            LocalDateTime localDateTime = LocalDateTime.now();
            String time = localDateTime.format(formatter);

            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, clanName);
            preparedStatement.setInt(3, group);
            preparedStatement.setString(4, time);
            preparedStatement.setInt(5, clanID);
            preparedStatement.executeUpdate();

            // Обновляет количество игроков на +1
            repository.setAmountPlayer(clanName, repository.getAmountPlayer(clanName) + 1);
            // Добавляет в арай лист игрока который лежит в хэш мапе
            SystemClans.getPlayersInClan().get(clanName).add(player);
            // Добавляет игрока клан игрока в мапу
            SystemClans.getClanNameByPlayer().put(player, clanName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Удаляет игрока из базы данных.
     *
     * @param clanName     Название клана, к которому принадлежит игрок
     * @param deletePlayer Объект Player, представляющий игрока, которого нужно удалить
     */
    public void deletePlayer(String clanName, Player deletePlayer){
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM players WHERE user_name = ?")) {
            preparedStatement.setString(1, deletePlayer.getName());
            preparedStatement.executeUpdate();

            ClanRepository repository = new ClanRepository(connection);

            // Обновляет количество игроков на -1
            repository.setAmountPlayer(clanName, repository.getAmountPlayer(clanName) - 1);
            // Удаляет из арай лист игрока который лежит в хэш мапе
            ArrayList<Player> players = SystemClans.getPlayersInClan().get(clanName);
            players.remove(deletePlayer);
            // Удаляет игрока клан игрока из мапы
            SystemClans.getClanNameByPlayer().remove(deletePlayer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получает группу игрока по его имени.
     *
     * @param userName Имя игрока
     * @return Группа игрока
     */
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

    /**
     * Устанавливает группу игрока.
     *
     * @param userName Имя игрока
     * @param group    Группа игрока
     */
    public void setPlayerGroup(String userName, int group){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET `group` = ? WHERE user_name = ? AND clan_id = ?")) {

            ClanRepository repository = new ClanRepository(connection);
            int claId =  repository.getClanIdByName(repository.getClanName(userName));

            preparedStatement.setInt(1, group);
            preparedStatement.setString(2, userName);
            preparedStatement.setInt(3, claId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получает список всех игроков для указанного клана.
     *
     * @param clanName Название клана
     * @return Список имен игроков
     */
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

    /**
     * Преобразует числовое значение группы в строковое представление.
     *
     * @param group Числовое значение группы
     * @return Строковое представление группы
     */
    public String groupToString(int group){
        if (group == 0){
            return "Игрок";
        } else if (group == 1) {
            return "Доверенный";
        }else {
            return "OWN";
        }
    }

    /**
     * Получает количество убийств для указанного игрока.
     *
     * @param userName Имя игрока
     * @return Количество убийств игрока
     */
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

    /**
     * Увеличивает количество убийств для указанного игрока на 1.
     *
     * @param userName Имя игрока
     */
    public String getDateAdd(String userName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT date_add FROM players WHERE user_name = ?")) {
            preparedStatement.setString(1, userName);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("date_add");
            }else {
                return "";
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
