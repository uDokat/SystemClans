package org.dokat.systemclans.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.controllers.ClanController;
import org.dokat.systemclans.dbmanagement.controllers.PlayerController;
import org.dokat.systemclans.utils.Utility;

public class ClanDeleteSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForDelete = config.getClanSettings("permission_for_delete");
    private final String clanDeleted = config.getMessages("clan_deleted");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String notClan = config.getMessages("not_clan");
    private final String commandFailed = config.getMessages("command_failed");

    @Override
    public boolean execute(CommandSender sender, String[] args){
        Player player = (Player) sender;
        String userName = player.getName();

        Bukkit.getScheduler().runTaskAsynchronously(SystemClans.getInstance(), () -> {
            if(PlayerController.isHaveClan(userName)){
                if (PlayerController.getPlayer(userName).getGroup() >= permissionForDelete){
                    if (args.length == 0){
                        ClanController.delete(ClanController.getClan(PlayerController.getPlayer(userName).getClanName()));
                        player.sendMessage(color(clanDeleted));
                    }else {
                        player.sendMessage(color(commandFailed));
                    }
                }else {
                    player.sendMessage(color(lackOfRights));
                }
            }else {
                player.sendMessage(color(notClan));
            }
        });

        return true;
    }
}
