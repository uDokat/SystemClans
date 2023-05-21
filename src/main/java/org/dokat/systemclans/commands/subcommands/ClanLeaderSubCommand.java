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

public class ClanLeaderSubCommand implements SubCommand {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForRename = config.getClanSettings("permission_for_rename");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String playerNotInClan = config.getMessages("player_not_in_clan");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Player targetPlayer = Bukkit.getPlayer(args[0]);

        String userName = player.getName();
        String targetUserName = targetPlayer.getName();

        Connection connection = SystemClans.getConnection();
        ClanStatusCache cache = new ClanStatusCache(connection, SystemClans.getCache());
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = cache.getClanName(userName);
        String targetClanName = cache.getClanName(targetUserName);

        if (args.length == 1){
            if (clanName.equals(targetClanName)){
                if (playerRepository.getPlayerGroup(userName) == permissionForRename){
                    playerRepository.setPlayerGroup(targetUserName, 2);
                    playerRepository.setPlayerGroup(userName, 0);
                    player.sendMessage(color(config.getMessages("permission_betrayed").replace("{targetUserName}", targetUserName)));
                }else {
                    player.sendMessage(color(lackOfRights));
                }
            }else {
                player.sendMessage(color(playerNotInClan));
            }
        }

        return true;
    }
}
