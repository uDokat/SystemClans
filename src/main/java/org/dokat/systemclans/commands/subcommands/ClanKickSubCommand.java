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

public class ClanKickSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForKick = config.getClanSettings("permission_for_kick");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String playerNotInClan = config.getMessages("player_not_in_clan");
    private final String commandFailed = config.getMessages("command_failed");
    private final String playerKicked = config.getMessages("player_kicked");
    private final String playerKickedCause = config.getMessages("player_kicked_cause");
    private final String youKickedFromClanCause = config.getMessages("you_kicked_from_clan_cause");
    private final String youKickedFromClan = config.getMessages("you_kicked_from_clan");
    private final String clanLeaveFailed = config.getMessages("clan_leave_failed");

    @Override
    public boolean execute(CommandSender sender, String[] args){
        Player player = (Player) sender;
        Player targetPlayer = Bukkit.getPlayer(args[0]);

        String userName = player.getName();
        String targetUserName = targetPlayer.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, "");
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String playerKickedMessage;
        String youKickedFromClanMessage;
        String cause = "";
        if (args.length == 2){
            cause = args[1];
             playerKickedMessage = playerKickedCause
                    .replace("{targetUserName}", targetUserName)
                    .replace("{userName}", userName)
                    .replace("{cause}", cause);
            youKickedFromClanMessage = youKickedFromClanCause
                    .replace("{targetUserName}", targetUserName)
                    .replace("{userName}", userName)
                    .replace("{cause}", cause);
        }else {
            playerKickedMessage = playerKicked
                    .replace("{targetUserName}", targetUserName)
                    .replace("{userName}", userName);
            youKickedFromClanMessage = youKickedFromClan
                    .replace("{targetUserName}", targetUserName)
                    .replace("{userName}", userName);
        }

        String clanName = clanRepository.getClanName(userName);

        if (clanName != null){
            if (playerRepository.getPlayerGroup(userName) >= permissionForKick){
                if (!targetUserName.equalsIgnoreCase(userName)){
                    if (args.length <= 2){
                        playerRepository.deletePlayer(userName, targetUserName);
                        targetPlayer.sendMessage(color(youKickedFromClanMessage));
                        sendMessageEveryone(clanName, playerKickedMessage, targetUserName);
                    }else {
                        player.sendMessage(color(commandFailed));
                    }
                }else {
                    player.sendMessage(color(clanLeaveFailed));
                }
            }else {
                player.sendMessage(color(lackOfRights));
            }
        }else {
            player.sendMessage(color(playerNotInClan));
        }

        return true;
    }
}
