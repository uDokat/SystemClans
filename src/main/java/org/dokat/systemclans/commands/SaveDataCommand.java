package org.dokat.systemclans.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.controllers.ClanController;
import org.dokat.systemclans.dbmanagement.controllers.DataController;
import org.dokat.systemclans.dbmanagement.controllers.PlayerController;
import org.dokat.systemclans.dbmanagement.data_models.Clan;
import org.dokat.systemclans.dbmanagement.data_models.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SaveDataCommand implements CommandExecutor {

    public SaveDataCommand(){
        PluginCommand pluginCommand = SystemClans.getInstance().getCommand("save");
        assert pluginCommand != null;
        pluginCommand.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Bukkit.getScheduler().runTaskAsynchronously(SystemClans.getInstance(), () -> {
            ArrayList<Clan> clans = DataController.getClans();
            ArrayList<Player> players = DataController.getPlayers();

            if (clans.isEmpty() && players.isEmpty()){
                commandSender.sendMessage("Нет данных ожидающие записи");
                return;
            }

            for (Clan clan : clans){
                ClanController.save(clan);
            }
            clans.clear();

            for (Player player : players){
                commandSender.sendMessage(player.getUserName());
                PlayerController.save(player);
                commandSender.sendMessage("da");
            }
            players.clear();

        });

        return true;
    }
}
