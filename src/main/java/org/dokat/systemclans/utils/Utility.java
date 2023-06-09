package org.dokat.systemclans.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.util.List;

/**
 * Интерфейс Utility предоставляет утилитарные методы, используемые в различных классах.
 */
public interface Utility {

    /**
     * Преобразует строку, заменяя альтернативные цветовые коды в тексте на соответствующие цвета Minecraft.
     *
     * @param string исходная строка
     * @return строка с преобразованными цветовыми кодами
     */
    default String color(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Отправляет сообщение всем игрокам в клане, за исключением определенного игрока (если указан).
     *
     * @param clanName    имя клана
     * @param message     сообщение для отправки
     * @param targetName  имя игрока, которому не нужно отправлять сообщение (может быть null)
     */
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
