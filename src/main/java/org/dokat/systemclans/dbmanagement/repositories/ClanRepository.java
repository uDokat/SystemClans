package org.dokat.systemclans.dbmanagement.repositories;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Репозиторий для работы с кланами в базе данных.
 */
public class ClanRepository {

    private final Connection connection;
    private final ConfigManager config = new ConfigManager();
    private final int amountReputation = config.getClanSettings("amount_reputation_for_kills");
    private final int amountKills = config.getClanSettings("reputation_for_amount_kills");
    private final SystemClans instance = SystemClans.getInstance();

    /**
     * Конструктор класса ClanRepository.
     *
     * @param connection соединение с базой данных
     */
    public ClanRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * Создает новый клан в базе данных и связывает игрока с кланом.
     *
     * @param clanName имя клана
     * @param player   игрок, создающий клан
     */
    public void createClan(String clanName, Player player){
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run() {
                try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO clans (clan_name, level, balance, amount_player, date_create) VALUES (?, ?, ?, ?, ?)")) {
                    preparedStatement.setString(1, clanName);
                    preparedStatement.setInt(2, 0);
                    preparedStatement.setInt(3, 0);
                    preparedStatement.setInt(4, 0);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
                    LocalDateTime localDateTime = LocalDateTime.now();
                    String time = localDateTime.format(formatter);

                    preparedStatement.setString(5, time);
                    preparedStatement.executeUpdate();

                    //Создаёт новую запись клана в хэшмапе
                    SystemClans.setPlayersInClan(clanName, new ArrayList<>());

                    //Сохраняет игрока в таблицу players
                    PlayerRepository repository = new PlayerRepository(connection);
                    repository.savePlayer(player, clanName, 2);

                    //Создаёт запись пвп статуса в хэшмапе
                    SystemClans.getStatusPvp().put(clanName, true);
                    //Создаёт запись к какому клану относится игрок
                    SystemClans.getClanNameByPlayer().put(player, clanName);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(instance);

        task.cancel();
    }

    /**
     * Удаляет клан из базы данных и освобождает всех игроков, принадлежащих клану.
     *
     * @param userName имя игрока, выполняющего удаление клана
     */
    public void deleteClan(String userName){
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                try (PreparedStatement preparedStatementClan = connection.prepareStatement("DELETE FROM clans WHERE id = ?");
                     PreparedStatement preparedStatementPlayer = connection.prepareStatement("DELETE FROM players WHERE clan_id = ?")) {

                    String clanName = getClanName(userName);
                    //Получаем id клана
                    int clanId = getClanIdByName(clanName);

                    //Если у клана есть клановый хом, то удаляет запись о клан хоме
                    if (getLocationClanHome(clanName) != null){
                        PreparedStatement preparedStatementHome = connection.prepareStatement("DELETE FROM clan_houses WHERE clan_id = ?");
                        preparedStatementHome.setInt(1, clanId);
                        preparedStatementHome.executeUpdate();
                    }

                    preparedStatementPlayer.setInt(1, clanId);
                    preparedStatementPlayer.executeUpdate();

                    preparedStatementClan.setInt(1, clanId);
                    preparedStatementClan.executeUpdate();

                    //Удаляет всех игроков их хэшмапы и саму запись в ней
                    ArrayList<Player> players = SystemClans.getPlayersInClan().get(clanName);
                    for (Player player : players){
                        SystemClans.getClanNameByPlayer().remove(player);
                    }

                    SystemClans.getPlayersInClan().remove(clanName);
                    //Удаляет запись о статусе пвп клана
                    SystemClans.getStatusPvp().remove(clanName);

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(instance);

        task.cancel();
    }

    /**
     * Проверяет, существует ли клан с указанным именем.
     *
     * @param clanName имя клана
     * @return true, если клан с указанным именем не найден; в противном случае false
     */
    public boolean isClanNameNotFound(String clanName){
        final boolean[] isFound = {false};

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM clans WHERE clan_name = ?")) {
                    statement.setString(1, clanName);

                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        isFound[0] = count == 0;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(instance);

        task.cancel();
        return isFound[0];
    }

    /**
     * Метод для получения идентификатора клана по его названию.
     *
     * @param clanName название клана
     * @return идентификатор клана
     */
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


//        BukkitTask task = new BukkitRunnable() {
//            @Override
//            public void run() {
//
//                return false;
//            }
//        }.runTaskAsynchronously(instance);
//
//        task.cancel();
    }

    /**
     * Метод для получения названия клана по имени игрока.
     *
     * @param userName имя игрока
     * @return название клана
     */
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

    /**
     * Метод для изменения названия клана.
     *
     * @param clanName      текущее название клана
     * @param newClanName   новое название клана
     */
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

            //Удаляет старые записи с старым именем клана и создаёт новые
            ArrayList<Player> players2 = SystemClans.getPlayersInClan().get(clanName);
            HashMap<Player, String> isSameClan = SystemClans.getClanNameByPlayer();

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

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

            }
        }.runTaskAsynchronously(instance);

        task.cancel();
    }

    /**
     * Метод для получения уровня клана по его названию.
     *
     * @param clanName название клана
     * @return уровень клана
     */
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

    /**
     * Метод для установки уровня клана.
     *
     * @param clanName название клана
     */
    public void setClanLevel(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET level = ? WHERE id = ?")) {
            preparedStatement.setInt(1, getClanLevel(clanName) + 1);
            preparedStatement.setInt(2, getClanIdByName(clanName));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод для получения баланса клана по его названию.
     *
     * @param clanName название клана
     * @return баланс клана
     */
    public int getClanBalance(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT balance FROM clans WHERE id = ?")) {
            preparedStatement.setInt(1, getClanIdByName(clanName));

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

    /**
     * Метод для установки баланса клана.
     *
     * @param clanName название клана
     * @param amount   сумма
     */
    public void setClanBalance(String clanName, int amount){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET balance = ? WHERE id = ?")) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setInt(2, getClanIdByName(clanName));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Метод для получения количества игроков в клане.
     *
     * @param clanName название клана
     * @return количество игроков
     */
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

    /**
     * Устанавливает количество игроков для указанного клана.
     *
     * @param clanName Название клана
     * @param amountPlayer Количество игроков
     */
    public void setAmountPlayer(String clanName, int amountPlayer){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET amount_player = ? WHERE id = ?")) {
            preparedStatement.setInt(1, amountPlayer);
            preparedStatement.setInt(2, getClanIdByName(clanName));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получает местоположение дома клана.
     *
     * @param clanName Название клана
     * @return Местоположение дома клана (объект класса Location), либо null, если дом не найден
     */
    public Location getLocationClanHome(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT x, y, z, world_name FROM clan_houses WHERE clan_id = ?")) {
            int clanId = getClanIdByName(clanName);
            preparedStatement.setDouble(1, clanId);

            double x;
            double y;
            double z;
            String worldName;

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

    /**
     * Создает местоположение дома клана.
     *
     * @param clanName   Название клана
     * @param x          Координата X
     * @param y          Координата Y
     * @param z          Координата Z
     * @param worldName  Название мира
     */
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

    /**
     * Обновляет местоположение дома клана.
     * Если местоположение дома не существует, то создает новое.
     *
     * @param clanName   Название клана
     * @param x          Координата X
     * @param y          Координата Y
     * @param z          Координата Z
     * @param worldName  Название мира
     */
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

    /**
     * Получает приветственное сообщение для указанного клана.
     *
     * @param clanName Название клана
     * @return Приветственное сообщение, либо null, если сообщение не найдено
     */
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

    /**
     * Устанавливает приветственное сообщение для указанного клана.
     *
     * @param clanName       Название клана
     * @param welcomeMessage Приветственное сообщение
     */
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

    /**
     * Получает статус PvP для указанного клана.
     *
     * @param clanName Название клана
     * @return true, если PvP включен, иначе false
     */
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

    /**
     * Устанавливает статус PvP для указанного клана.
     *
     * @param clanName    Название клана
     * @param statusPvp   Статус PvP (true - включен, false - выключен)
     */
    public void setStatusPvp(String clanName, boolean statusPvp){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET pvp = ? WHERE id = ?")) {
            preparedStatement.setBoolean(1, statusPvp);
            preparedStatement.setInt(2, getClanIdByName(clanName));
            preparedStatement.executeUpdate();

            // Перезаписывает статус пвп в мапе
            SystemClans.getStatusPvp().remove(clanName);
            SystemClans.getStatusPvp().put(clanName, statusPvp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Добавляет статус PvP в мапу.
     */
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

    /**
     * Получает количество убийств для указанного клана.
     *
     * @param clanName Название клана
     * @return Количество убийств
     */
    public int getAmountkills(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT kills FROM clans WHERE id = ?")) {
            preparedStatement.setInt(1, getClanIdByName(clanName));

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("kills");
            }else {
                resultSet.close();
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Увеличивает количество убийств для указанного клана на 1.
     * При достижении определенного количества убийств также увеличивает репутацию клана.
     *
     * @param clanName Название клана
     */
    public void addAmountkills(String clanName){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET kills = ? WHERE id = ?")) {

            preparedStatement.setInt(1,  getAmountkills(clanName) + 1);
            preparedStatement.setInt(2, getClanIdByName(clanName));
            preparedStatement.executeUpdate();

            if (getAmountkills(clanName) % amountKills == 0){
                setReputation(clanName, getReputation(clanName) + amountReputation);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получает репутацию для указанного клана.
     *
     * @param clanName Название клана
     * @return Репутация клана
     */
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

    /**
     * Устанавливает репутацию для указанного клана.
     *
     * @param clanName   Название клана
     * @param reputation Репутация клана
     */
    public void setReputation(String clanName, int reputation){
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clans SET reputation = ? WHERE id = ?")) {
            preparedStatement.setInt(1, reputation);
            preparedStatement.setInt(2, getClanIdByName(clanName));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получает дату создания указанного клана.
     *
     * @param clanName Название клана
     * @return Дата создания клана
     */
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