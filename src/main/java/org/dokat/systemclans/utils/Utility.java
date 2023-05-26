package org.dokat.systemclans.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.util.List;

public interface Utility {

    default String color(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    default void sendMessageEveryone(String clanName, String message, String targetName){
        PlayerRepository playerRepository = new PlayerRepository(SystemClans.getConnection());

        List<String> players = playerRepository.getAllPlayersForClanName(clanName);

        for (String p : players){
            Player player = Bukkit.getPlayer(p);
            if (player != null && !p.equalsIgnoreCase(targetName)){
                player.sendMessage(color(message));
            }
        }
    }
}
