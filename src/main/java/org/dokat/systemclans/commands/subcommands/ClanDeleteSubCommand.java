package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.sql.Connection;

public class ClanDeleteSubCommand implements SubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args){
        Player player = (Player) sender;

        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, userName);
        PlayerRepository playerRepository = new PlayerRepository(connection);
        ConfigManager config = new ConfigManager();

        if(clanRepository.getClanStatus(userName) != null){
            if (playerRepository.getPlayerGroup(userName) >= 1){
                clanRepository.deleteClan(userName);
                player.sendMessage(color(config.getMessages("clan_deleted")));
            }else {
                player.sendMessage(color(config.getMessages("lack_of_rights")));
            }
        }else {
            player.sendMessage(color(config.getMessages("not_clan")));
        }

        return true;
    }
}
