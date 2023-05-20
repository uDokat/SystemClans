package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class ClanRenameSubCommand implements SubCommand{

    private ConfigManager config = new ConfigManager();
    private int priceRename = config.getClanSettings("price_rename");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, userName);

        String clanName = clanRepository.getClanStatus(userName);
        String newClanName = args[0].toUpperCase();

        if (clanName != null){
            if (clanRepository.getClanBalance() >= priceRename){
                if (newClanName.length() == 3){
                    clanRepository.setClanBalance(clanName, priceRename);
                    clanRepository.setClanName(clanName, newClanName);
                }else {
                    player.sendMessage(color(""));
                }
            }else {
                player.sendMessage(color(""));
            }
        }else {
            player.sendMessage("");
        }

        return true;
    }
}
