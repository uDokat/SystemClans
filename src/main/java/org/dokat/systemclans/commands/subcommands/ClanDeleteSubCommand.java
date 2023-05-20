package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.sql.Connection;

public class ClanDeleteSubCommand implements SubCommand {

    private final ConfigManager config = new ConfigManager();
    private final String clanDeleted = config.getMessages("clan_deleted");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String notClan = config.getMessages("not_clan");

    @Override
    public boolean execute(CommandSender sender, String[] args){
        Player player = (Player) sender;

        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, userName);
        PlayerRepository playerRepository = new PlayerRepository(connection);

        if(clanRepository.getClanStatus(userName) != null){
            if (playerRepository.getPlayerGroup(userName) >= 1){
                clanRepository.deleteClan(userName);
                player.sendMessage(color(clanDeleted));
            }else {
                player.sendMessage(color(lackOfRights));
            }
        }else {
            player.sendMessage(color(notClan));
        }

        return true;
    }
}
