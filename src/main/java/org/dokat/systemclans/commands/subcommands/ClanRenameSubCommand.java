package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;
import org.dokat.systemclans.utils.Utility;

import java.sql.Connection;

public class ClanRenameSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForRename = config.getClanSettings("permission_for_rename");
    private final int priceRename = config.getClanSettings("price_rename");
    private final String clanRenamed = config.getMessages("clan_renamed");
    private final String clanRenameFailed = config.getMessages("clan_name_failed");
    private final String notEnoughMoney = config.getMessages("not_enough_money");
    private final String notClan = config.getMessages("not_clan");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String commandFailed = config.getMessages("command_failed");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, userName);

        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = clanRepository.getClanName(userName);

        if (clanName != null){
            if (args.length == 1){
                String newClanName = args[0].toUpperCase();
                if (newClanName.length() == 3 && clanRepository.isClanNameNotFound(newClanName)){
                    if (playerRepository.getPlayerGroup(userName) >= permissionForRename){
                        if (clanRepository.getClanBalance() >= priceRename){
                            clanRepository.setClanBalance(clanName, priceRename);
                            clanRepository.setClanName(clanName, newClanName);
                            sendMessageEveryone(newClanName, clanRenamed.replace("{newClanName}", newClanName), null);
                        }else {
                            player.sendMessage(color(notEnoughMoney));
                        }
                    }else {
                        player.sendMessage(color(lackOfRights));
                    }
                }else {
                    player.sendMessage(color(clanRenameFailed));
                }
            }else {
                player.sendMessage(color(commandFailed));
            }
        }else {
            player.sendMessage(color(notClan));
        }

        return true;
    }
}
