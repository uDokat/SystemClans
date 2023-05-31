package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.utils.Utility;

import java.sql.Connection;

public class ClanCreateSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int clanNameLength = config.getClanSettings("clan_name_length");
    private final String clanNameFailed = config.getMessages("clan_name_failed");
    private final String youAlreadyInClan = config.getMessages("you_already_in_clan");
    private final String commandFailed = config.getMessages("command_failed");

    @Override
    public boolean execute(CommandSender sender, String[] args){
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection);

        if (clanRepository.getClanName(userName) == null){
            if (args.length == 1){
                String clanName = args[0].toUpperCase();
                if (clanName.length() == clanNameLength && clanRepository.isClanNameNotFound(clanName)){
                    clanRepository.createClan(clanName, player);
                    player.sendMessage(color(config.getMessages("clan_created").replace("{clanName}", clanName)));
                }else {
                    player.sendMessage(color(clanNameFailed));
                }
            }else {
                player.sendMessage(color(commandFailed));
            }
        }else {
            player.sendMessage(color(youAlreadyInClan));
        }


        return true;
    }
}
