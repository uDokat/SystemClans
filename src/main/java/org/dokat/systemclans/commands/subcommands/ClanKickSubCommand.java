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
    @Override
    public boolean execute(CommandSender sender, String[] args){
        Player player = (Player) sender;
        Player player1 = Bukkit.getPlayer(args[0]);

        String userName = player.getName();
        String userName1 = player1.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, userName);
        PlayerRepository playerRepository = new PlayerRepository(connection);
        ConfigManager config = new ConfigManager();

        if (args.length == 1){
            if (clanRepository.getClanStatus(userName) != null){
                if (playerRepository.getPlayerGroup(userName) >= 1){
                    playerRepository.deletePlayer(userName, userName1);
                    player.sendMessage(color(config.getMessages("player_kicked").replace("{userName1}", userName1)));
                }else {
                    player.sendMessage(color(config.getMessages("lack_of_rights")));
                }
            }else {
                player.sendMessage(color(config.getMessages("player_not_in_clan")));
            }
        }

        return true;
    }
}
