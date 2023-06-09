package org.dokat.systemclans.management;

import org.bukkit.entity.Player;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс ClanChatManager обрабатывает отправку сообщений в чате клана.
 */
public class ClanChatManager implements Utility {

    private final HashMap<String, ArrayList<Player>> playerInClan = SystemClans.getPlayersInClan();

    /**
     * Отправляет сообщение в чате клана.
     *
     * @param clanName имя клана
     * @param sender   игрок, отправляющий сообщение
     * @param message  текст сообщения
     */
    public void sendClanChatMessage(String clanName, Player sender,  String message){
        if (playerInClan.get(clanName) != null){
            ArrayList<Player> players = playerInClan.get(clanName);

            for (Player player : players){
                if (player != null && player.isOnline()){
                    String formattedMessage = String.format("&6[CC] %s%s&6: %s",
                            "&f", sender.getName(), message);

                    player.sendMessage(color(formattedMessage));
                }
            }
        }
    }
}
