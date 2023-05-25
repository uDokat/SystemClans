package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;
import org.dokat.systemclans.utils.Utility;

import java.sql.Connection;

public class ClanWelcomeSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForSetWelcomeMessage = config.getClanSettings("permission_for_set_welcome_message");
    private final String welcomeMessageCreated = config.getMessages("welcome_message_created");
    private final String welcomeFailed = config.getMessages("welcome_failed");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String notClan = config.getMessages("not_clan");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        String message = String.join(" ", args);

        Connection connection = SystemClans.getConnection();

        ClanRepository clanRepository = new ClanRepository(connection, userName);
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = clanRepository.getClanName(userName);

        if (clanName != null){
            if (playerRepository.getPlayerGroup(userName) >= permissionForSetWelcomeMessage){
                if (args.length > 0){
                    clanRepository.setWelcomeMessage(clanName, message);
                    player.sendMessage(color(welcomeMessageCreated));
                }else {
                    player.sendMessage(color(welcomeFailed));
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
