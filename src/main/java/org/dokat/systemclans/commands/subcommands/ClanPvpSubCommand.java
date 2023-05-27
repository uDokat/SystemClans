package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;
import org.dokat.systemclans.utils.Utility;

import java.sql.Connection;

public class ClanPvpSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForSetPvp = config.getClanSettings("permission_for_set_pvp");
    private final String pvpDisabled = config.getMessages("pvp_disabled");
    private final String pvpEnabled = config.getMessages("pvp_enabled");
    private final String commandFailed = config.getMessages("command_failed");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String notClan = config.getMessages("not_clan");


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, "");
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = clanRepository.getClanName(userName);

        boolean statusPvp = args[0].equalsIgnoreCase("on");

        if (clanName != null){
            if (playerRepository.getPlayerGroup(userName) >= permissionForSetPvp){
                if (args.length == 1 && args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")){
                    clanRepository.setStatusPvp(clanName, statusPvp);
                    if (statusPvp){
                        sendMessageEveryone(clanName, pvpEnabled, null);
                    }else {
                        sendMessageEveryone(clanName, pvpDisabled, null);
                    }
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
