package org.dokat.systemclans.chat_managment;

import org.bukkit.entity.Player;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;

public class ClanChatManager implements Utility {

    private final HashMap<String, ArrayList<Player>> playerInClan = SystemClans.getPlayersInClan();

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
