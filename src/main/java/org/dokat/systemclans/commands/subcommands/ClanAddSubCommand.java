package org.dokat.systemclans.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;
import org.dokat.systemclans.management.ClanInviteManager;
import org.dokat.systemclans.utils.Utility;

import java.sql.Connection;

public class ClanAddSubCommand implements SubCommand, Utility {
    private ClanInviteManager clanInviteManager = SystemClans.getClanInviteManager();

    private final ConfigManager config = new ConfigManager();
    private final int permissionForAdd = config.getClanSettings("permission_for_add");
    private final String playerAlreadyInClan = config.getMessages("player_already_in_clan");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String commandFailed = config.getMessages("command_failed");
    private final String notClan = config.getMessages("not_clan");

    @Override
    public boolean execute(CommandSender sender, String[] args){
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

        ClanRepository clanRepository = new ClanRepository(connection, userName);
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = clanRepository.getClanName(userName);
        String targetClanName = clanRepository.getClanName(targetUserName);

        if (clanName != null){
            if (playerRepository.getPlayerGroup(userName) >= permissionForAdd){
                if (targetClanName == null){
                    clanInviteManager.sendInvite(player, targetPlayer);
                }else {
                    player.sendMessage(color(playerAlreadyInClan));
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
