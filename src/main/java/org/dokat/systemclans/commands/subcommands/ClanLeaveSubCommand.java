package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.sql.Connection;

public class ClanLeaveSubCommand implements SubCommand{

    private final ConfigManager config = new ConfigManager();
    private final String clanLeave = config.getMessages("clan_leave");
    private final String commandFailed = config.getMessages("command_failed");
    private final String clanLeaveFailed = config.getMessages("clan_leave_failed");
    private final String notClan = config.getMessages("not_clan");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, userName);
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = clanRepository.getClanName(userName);

        if (clanName != null){
            if (playerRepository.getPlayerGroup(userName) < 2){
                if (args.length == 0){
                    playerRepository.deletePlayer(clanName, userName);
                    player.sendMessage(color(clanLeave));
                }else {
                    player.sendMessage(color(commandFailed));
                }
            }else {
                player.sendMessage(color(clanLeaveFailed));
            }
        }else {
            player.sendMessage(color(notClan));
        }

        return true;
    }
}
