package org.dokat.systemclans.commands.subcommands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.controllers.ClanController;
import org.dokat.systemclans.dbmanagement.controllers.ClanHomeController;
import org.dokat.systemclans.dbmanagement.controllers.PlayerController;
import org.dokat.systemclans.dbmanagement.data_models.Clan;
import org.dokat.systemclans.dbmanagement.data_models.ClanHome;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;
import org.dokat.systemclans.utils.Utility;

import java.sql.Connection;

public class ClanSetHomeSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForSethome = config.getClanSettings("permission_for_sethome");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String notClan = config.getMessages("not_clan");
    private final String clanHomeCreated = config.getMessages("clan_home_created");
    private final String commandFailed = config.getMessages("command_failed");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();
        Location playerLocation = player.getLocation();

        org.dokat.systemclans.dbmanagement.data_models.Player dataPlayer = PlayerController.getPlayer(userName);

        if (PlayerController.isHaveClan(userName)){
            if (dataPlayer.getGroup() >= permissionForSethome){
                if (args.length == 0){
                    ClanHomeController.save(new ClanHome(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), player.getWorld().getName()), ClanController.getClan(dataPlayer.getClanName()).getId());
                    sendMessageEveryone(dataPlayer.getClanName(), clanHomeCreated, null);
                }else {
                    player.sendMessage(color(commandFailed));
                }
            }else {
                player.sendMessage(color(lackOfRights));
            }
        }else {
            player.sendMessage(color(notClan));
        }

        return true;
    }
}
