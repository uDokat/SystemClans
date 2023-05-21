package org.dokat.systemclans.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.cache.ClanStatusCache;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.sql.Connection;

public class ClanSetRankSubCommand implements SubCommand {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForUpRank = config.getClanSettings("permission_for_up_rank");
    private final String groupCannotBeRaised = config.getMessages("group_cannot_be_raised");
    private final String groupReducedToMinimumLevel = config.getMessages("group_reduced_to_minimum_level");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String playerNotInClan = config.getMessages("player_not_in_clan");
    private final String commandFailed = config.getMessages("command_failed");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Player targetPlayer = Bukkit.getPlayer(args[0]);

        String userName = player.getName();
        String targetUserName = targetPlayer.getName();

        Connection connection = SystemClans.getConnection();
        ClanStatusCache cache = new ClanStatusCache(connection, SystemClans.getCache());
        PlayerRepository playerRepository = new PlayerRepository(connection);

        int userGroup = playerRepository.getPlayerGroup(userName);
        int targetUserGroup = playerRepository.getPlayerGroup(targetUserName);

        String userClan = cache.getClanName(userName);
        String targetUserClan = cache.getClanName(targetUserName);

        if (args.length == 2){
            if (userClan.equals(targetUserClan)){
                if (userGroup == permissionForUpRank){
                    if (args[1].equalsIgnoreCase("up")){
                        if (targetUserGroup == 0){
                            playerRepository.setPlayerGroup(targetUserName, 1);
                            player.sendMessage(color(config.getMessages("group_player_up")
                                    .replace("{targetUserName}", targetUserName)
                                    .replace("{group}", playerRepository.groupToString(1))));
                        }else {
                            player.sendMessage(color(groupCannotBeRaised));
                        }
                    } else if (args[1].equalsIgnoreCase("down")) {
                        if (targetUserGroup == 1){
                            playerRepository.setPlayerGroup(targetUserName, 0);
                            player.sendMessage(color(config.getMessages("group_player_down")
                                    .replace("{targetUserName}", targetUserName)
                                    .replace("{group}", playerRepository.groupToString(0))));
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
