package org.dokat.systemclans.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.sql.Connection;

public class ClanKickSubCommand implements SubCommand {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForKick = config.getClanSettings("permission_for_kick");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String playerNotInClan = config.getMessages("player_not_in_clan");
    private final String commandFailed = config.getMessages("command_failed");

    @Override
    public boolean execute(CommandSender sender, String[] args){
        Player player = (Player) sender;
        Player targetPlayer = Bukkit.getPlayer(args[0]);

        String userName = player.getName();
        String targetUserName = targetPlayer.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, "");
        PlayerRepository playerRepository = new PlayerRepository(connection);

        if (clanRepository.getClanName(userName) != null){
            if (playerRepository.getPlayerGroup(userName) >= permissionForKick){
                if (args.length == 1){
                    playerRepository.deletePlayer(userName, targetUserName);
                    player.sendMessage(color(config.getMessages("player_kicked").replace("{targetUserName}", targetUserName)));
                }else {
                    player.sendMessage(color(commandFailed));
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
