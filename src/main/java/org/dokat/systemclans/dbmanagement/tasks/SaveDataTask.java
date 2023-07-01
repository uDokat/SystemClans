package org.dokat.systemclans.dbmanagement.tasks;

import org.bukkit.Bukkit;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.controllers.ClanController;
import org.dokat.systemclans.dbmanagement.controllers.DataController;
import org.dokat.systemclans.dbmanagement.controllers.PlayerController;
import org.dokat.systemclans.dbmanagement.data_models.Clan;
import org.dokat.systemclans.dbmanagement.data_models.Player;

import java.util.ArrayList;

public class SaveDataTask {

    public SaveDataTask(){
        save();
    }

    private void save(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(SystemClans.getInstance(), () -> {
            ArrayList<Clan> clans = DataController.getClans();
            ArrayList<Player> players = DataController.getPlayers();

            for (Clan clan : clans){
                ClanController.save(clan);
                clans.remove(clan);
            }

            for (Player player : players){
                PlayerController.save(player);
                players.remove(player);
            }
        }, 0, 36000);
    }
}
