package org.dokat.systemclans.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.controllers.ClanController;
import org.dokat.systemclans.dbmanagement.controllers.ClanHomeController;
import org.dokat.systemclans.dbmanagement.controllers.PlayerController;
import org.dokat.systemclans.utils.Utility;

public class ClanHomeSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final String clanHomeNotFound = config.getMessages("clan_home_not_found");
    private final String commandFailed = config.getMessages("command_failed");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Bukkit.getScheduler().runTaskAsynchronously(SystemClans.getInstance(), () -> {
            String clanName = PlayerController.getPlayer(userName).getClanName();
            int id = ClanController.getClan(clanName).getId();

            if (PlayerController.isHaveClan(userName)){
                if (ClanHomeController.isClanHomeExist(id)){
                    if (args.length == 0){
                        player.teleport(ClanHomeController.getClanHome(id));
                    }else {
                        player.sendMessage(color(commandFailed));
                    }
                }else {
                    player.sendMessage(color(clanHomeNotFound));
                }
            }
        });

        return true;
    }
}
