package org.dokat.systemclans.commands.subcommands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.cache.ClanStatusCache;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.sql.Connection;

public class ClanSetHomeSubCommand implements SubCommand{

    private final ConfigManager config = new ConfigManager();
    private final int permissionForSethome = config.getClanSettings("permission_for_sethome");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String notClan = config.getMessages("not_clan");
    private final String clanHomeCreated= config.getMessages("clan_home_created");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();
        Location playerLocation = player.getLocation();

        Connection connection = SystemClans.getConnection();
        ClanStatusCache cache = new ClanStatusCache(connection, SystemClans.getCache());
        ClanRepository clanRepository = new ClanRepository(connection, userName);
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = cache.getClanName(userName);

        if (clanName != null){
            if (playerRepository.getPlayerGroup(userName) >= permissionForSethome){
                clanRepository.updateLocationClanHome(clanName, playerLocation.getX(), playerLocation.getY(), playerLocation.getZ(), player.getWorld().getName());
                player.sendMessage(color(clanHomeCreated));
            }else {
                player.sendMessage(color(lackOfRights));
            }
        }else {
            player.sendMessage(color(notClan));
        }

        return true;
    }
}
