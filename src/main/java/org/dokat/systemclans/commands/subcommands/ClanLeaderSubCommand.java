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

public class ClanLeaderSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String playerNotInClan = config.getMessages("player_not_in_clan");
    private final String commandFailed = config.getMessages("command_failed");
    private final String permissionBetrayed = config.getMessages("permission_betrayed");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Player targetPlayer = null;

        if (args.length == 1){
            targetPlayer = Bukkit.getPlayer(args[0]);
        }else {
            player.sendMessage(color(commandFailed));
            return true;
        }

        String userName = player.getName();
        String targetUserName = targetPlayer.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection);
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = clanRepository.getClanName(userName);
        String targetClanName = clanRepository.getClanName(targetUserName);
        String permissionBetrayedMessage = permissionBetrayed.replace("{targetUserName}", targetUserName);

        if (clanName.equals(targetClanName)){
            if (playerRepository.getPlayerGroup(userName) == 2){
                playerRepository.setPlayerGroup(targetUserName, 2);
                playerRepository.setPlayerGroup(userName, 0);
                sendMessageEveryone(clanName, permissionBetrayedMessage, targetUserName);
            }else {
                player.sendMessage(color(lackOfRights));
            }
        }else {
            player.sendMessage(color(playerNotInClan));
        }

        return true;
    }
}
