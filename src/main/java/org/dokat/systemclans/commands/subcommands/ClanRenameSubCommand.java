package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.cache.ClanStatusCache;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;

import java.sql.Connection;

public class ClanRenameSubCommand implements SubCommand{

    private final ConfigManager config = new ConfigManager();
    private final int priceRename = config.getClanSettings("price_rename");
    private final String clanRenamed = config.getMessages("clan_renamed");
    private final String clanRenameFailed = config.getMessages("clan_name_failed");
    private final String notEnoughMoney = config.getMessages("not_enough_money");
    private final String youAlreadyInClan = config.getMessages("you_already_in_clan");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, userName);
        ClanStatusCache cache = new ClanStatusCache(connection, SystemClans.getCache());

        String clanName = cache.getClanName(userName);
        String newClanName = args[0].toUpperCase();

        if (clanName != null){
            if (clanRepository.getClanBalance() >= priceRename){
                if (newClanName.length() == 3 && clanRepository.isClanNameNotFound(clanName)){
                    clanRepository.setClanBalance(clanName, priceRename);
                    clanRepository.setClanName(clanName, newClanName);
                    player.sendMessage(color(clanRenamed));
                }else {
                    player.sendMessage(color(clanRenameFailed));
                }
            }else {
                player.sendMessage(color(notEnoughMoney));
            }
        }else {
            player.sendMessage(color(youAlreadyInClan));
        }

        return true;
    }
}
