package org.dokat.systemclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Класс PlayerJoinListener обрабатывает события входа игрока на сервер.
 */
public class PlayerJoinListener implements Listener {

    /**
     * Обрабатывает событие входа игрока на сервер.
     *
     * @param event событие входа игрока
     */
    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection);

        String clanName = null;
        if (clanRepository.getClanName(player.getName()) != null){
            clanName = clanRepository.getClanName(player.getName());
        }else {
            return;
        }

        // Если список игроков клана не существует, создается новый список
        if (SystemClans.getPlayersInClan().get(clanName) == null){
            ArrayList<Player> players = new ArrayList<>();
            players.add(player);
            SystemClans.setPlayersInClan(clanName, players);
            // Если список игроков клана уже существует, добавляется новый игрок
        }else {
            ArrayList<Player> players = SystemClans.getPlayersInClan().get(clanName);
            if (!players.contains(player)){
                players.add(player);
            }
        }
    }

    /**
     * Добавляет информацию о принадлежности игрока к клану при входе на сервер.
     *
     * @param event событие входа игрока
     */
    @EventHandler
    public void addPlayerData(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String userName = player.getName();

        ClanRepository clanRepository = new ClanRepository(SystemClans.getConnection());

        if (!SystemClans.getClanNameByPlayer().containsKey(player) && clanRepository.getClanName(userName) != null){
            SystemClans.getClanNameByPlayer().put(player, clanRepository.getClanName(userName));
        }
    }
}
