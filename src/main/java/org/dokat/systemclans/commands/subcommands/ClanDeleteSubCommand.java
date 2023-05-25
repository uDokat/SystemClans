package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;
import org.dokat.systemclans.utils.Utility;

import java.sql.Connection;

public class ClanDeleteSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForDelete = config.getClanSettings("permission_for_delete");
    private final String clanDeleted = config.getMessages("clan_deleted");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String notClan = config.getMessages("not_clan");
    private final String commandFailed = config.getMessages("command_failed");

    @Override
    public boolean execute(CommandSender sender, String[] args){
        Player player = (Player) sender;

        String userName = player.getName();

        Connection connection = SystemClans.getConnection();

        ClanRepository clanRepository = new ClanRepository(connection, userName);
        PlayerRepository playerRepository = new PlayerRepository(connection);

        if(clanRepository.getClanName(userName) != null){
            if (playerRepository.getPlayerGroup(userName) >= permissionForDelete){
                if (args.length == 0){
                    clanRepository.deleteClan(userName);
                    player.sendMessage(color(clanDeleted));
                }else {
                    player.sendMessage(color(commandFailed));
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
