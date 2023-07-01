package org.dokat.systemclans.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.controllers.ClanController;
import org.dokat.systemclans.dbmanagement.data_models.Clan;
import org.dokat.systemclans.dbmanagement.controllers.DataController;
import org.dokat.systemclans.dbmanagement.controllers.PlayerController;
import org.dokat.systemclans.management.PlayerBalanceManager;
import org.dokat.systemclans.utils.Utility;

public class ClanCreateSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int clanNameLength = config.getClanSettings("clan_name_length");
    private final int priceCreateClan = config.getClanSettings("price_create_clan");
    private final String clanNameFailed = config.getMessages("clan_name_failed");
    private final String youAlreadyInClan = config.getMessages("you_already_in_clan");
    private final String commandFailed = config.getMessages("command_failed");
    private final String noMoney = config.getMessages("no_money");

    @Override
    public boolean execute(CommandSender sender, String[] args){
        Player player = (Player) sender;
        String userName = player.getName();

        Bukkit.getScheduler().runTaskAsynchronously(SystemClans.getInstance(), () -> {
            PlayerBalanceManager manager = new PlayerBalanceManager();
            if (manager.getBalance(player) >= priceCreateClan){
                if (!PlayerController.isHaveClan(userName)){
                    if (args.length == 1){
                        String clanName = args[0].toUpperCase();
                        if (clanName.length() == clanNameLength && ClanController.isClanNameNotExist(clanName)){
                            ClanController.save(new Clan(clanName));
                            org.dokat.systemclans.dbmanagement.data_models.Player datePlayer = new org.dokat.systemclans.dbmanagement.data_models.Player(userName, clanName);
                            datePlayer.setGroup(2);
                            PlayerController.save(datePlayer);
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
            }else {
                player.sendMessage(color(noMoney));
            }
        });

        return true;
    }
}
