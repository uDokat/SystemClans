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

public class ClanAddSubCommand implements SubCommand {

    private final ConfigManager config = new ConfigManager();
    private final String playerAlreadyInClan = config.getMessages("player_already_in_clan");
    private final String lackOfRights = config.getMessages("lack_of_rights");

    @Override
    public boolean execute(CommandSender sender, String[] args){
        Player player = (Player) sender;
        Player targetPlayer = Bukkit.getPlayer(args[0]);

        String userName = player.getName();
        String targetUserName = targetPlayer.getName();

        Connection connection = SystemClans.getConnection();
        ClanStatusCache cache = new ClanStatusCache(connection, SystemClans.getCache());
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = cache.getClanName(userName);

        if (args.length == 1){
            if (playerRepository.getPlayerGroup(userName) >= 1){
                if (cache.getClanName(targetUserName) == null){
                    playerRepository.savePlayer(targetUserName, clanName, 0);
                    targetPlayer.sendMessage(color(config.getMessages("joined_clan").replace("{clanName}", clanName)));
                    player.sendMessage(color(config.getMessages("player_added").replace("{targetUserName}", targetUserName)));
                }else {
                    player.sendMessage(color(playerAlreadyInClan));
                }
            }else {
//                player.sendMessage(color("&c" + "Указанный игрок не найден или находится не в сети"));
                player.sendMessage(color(lackOfRights));
            }
        }

        return true;
    }
}
