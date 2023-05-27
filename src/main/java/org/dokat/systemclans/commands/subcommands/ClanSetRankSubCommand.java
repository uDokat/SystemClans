package org.dokat.systemclans.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;
import org.dokat.systemclans.utils.Utility;

import java.sql.Connection;

public class ClanSetRankSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForUpRank = config.getClanSettings("permission_for_up_rank");
    private final String groupCannotBeRaised = config.getMessages("group_cannot_be_raised");
    private final String groupReducedToMinimumLevel = config.getMessages("group_reduced_to_minimum_level");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String playerNotInClan = config.getMessages("player_not_in_clan");
    private final String commandFailed = config.getMessages("command_failed");
    private final String playerUp = config.getMessages("group_player_up");
    private final String playerDown = config.getMessages("group_player_down");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Player targetPlayer = null;

        if (args.length >= 1){
            targetPlayer = Bukkit.getPlayer(args[0]);
        }else {
            player.sendMessage(color(commandFailed));
            return true;
        }

        String userName = player.getName();
        String targetUserName = targetPlayer.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, "");
        PlayerRepository playerRepository = new PlayerRepository(connection);

        int userGroup = playerRepository.getPlayerGroup(userName);
        int targetUserGroup = playerRepository.getPlayerGroup(targetUserName);

        String userClan = clanRepository.getClanName(userName);
        String targetUserClan = clanRepository.getClanName(targetUserName);
        String playerUpMessage = playerUp.replace("{targetUserName}", targetUserName).replace("{group}", playerRepository.groupToString(1));
        String playerDownMessage = playerDown.replace("{targetUserName}", targetUserName).replace("{group}", playerRepository.groupToString(0));

        if (args.length == 2){
            if (userClan.equals(targetUserClan)){
                if (userGroup == permissionForUpRank){
                    if (args[1].equalsIgnoreCase("up")){
                        if (targetUserGroup == 0){
                            playerRepository.setPlayerGroup(targetUserName, 1);
                            sendMessageEveryone(userClan, playerUpMessage, targetUserName);
                        }else {
                            player.sendMessage(color(groupCannotBeRaised));
                        }
                    } else if (args[1].equalsIgnoreCase("down")) {
                        if (targetUserGroup == 1){
                            playerRepository.setPlayerGroup(targetUserName, 0);
                            sendMessageEveryone(userClan, playerDownMessage, targetUserName);
                        }
                    }else {
                        player.sendMessage(color(groupReducedToMinimumLevel));
                    }
                }else {
                    player.sendMessage(color(lackOfRights));
                }
            }else {
                player.sendMessage(color(playerNotInClan));
            }
        }else {
            player.sendMessage(color(commandFailed));
        }

        return true;
    }
}
