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

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanStatusCache cache = new ClanStatusCache(connection, SystemClans.getCache());
        ClanRepository clanRepository = new ClanRepository(connection, userName);

        String clanName = cache.getClanName(userName);

        if (clanName != null && clanRepository.getLocationClanHome(clanName) != null){
            player.teleport(clanRepository.getLocationClanHome(clanName));
        }else {
            player.sendMessage(color(clanHomeNotFound));
        }

        return true;
    }
}
