package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.cache.ClanStatusCache;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;

import java.sql.Connection;

public class ClanHomeSubCommand implements SubCommand{

    private final ConfigManager config = new ConfigManager();
    private final String clanHomeNotFound = config.getMessages("clan_home_not_found");
    private final String commandFailed = config.getMessages("command_failed");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();

        ClanRepository clanRepository = new ClanRepository(connection, userName);

        String clanName = clanRepository.getClanName(userName);

        if (clanName != null){
            if (clanRepository.getLocationClanHome(clanName) != null){
                if (args.length == 0){
                    player.teleport(clanRepository.getLocationClanHome(clanName));
                }else {
                    player.sendMessage(color(commandFailed));
                }
            }else {
                player.sendMessage(color(clanHomeNotFound));
            }
        }

        return true;
    }
}