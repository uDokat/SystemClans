package org.dokat.systemclans.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;

import java.util.List;

public interface Utility {

    default String color(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    default void sendMessageEveryone(String clanName, String message, String targetName){
        ClanRepository clanRepository = new ClanRepository(SystemClans.getConnection(), "");

        List<String> players = clanRepository.getAllPlayersForClanName(clanName);

        for (String p : players){
            Player player = Bukkit.getPlayer(p);
            if (player != null && !p.equalsIgnoreCase(targetName)){
                player.sendMessage(color(message));
            }
        }
    }
}
