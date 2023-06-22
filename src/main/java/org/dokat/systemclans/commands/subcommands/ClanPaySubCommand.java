package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;
import org.dokat.systemclans.management.PlayerBalanceManager;
import org.dokat.systemclans.utils.Utility;

import java.sql.Connection;

public class ClanPaySubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();

    private final int maxBalanceLevelZero = config.getClanSettings("max_balance_level_zero");
    private final int maxBalanceLevelOne = config.getClanSettings("max_balance_level_one");
    private final int maxBalanceLevelTwo = config.getClanSettings("max_balance_level_two");
    private final int maxBalanceLevelThree = config.getClanSettings("max_balance_level_three");

    private final String clanPay = config.getMessages("clan_pay");
    private final String payFailed = config.getMessages("clan_pay_failed");
    private final String youDontHaveMoney = config.getMessages("you_dont_have_money");
    private final String commandFailed = config.getMessages("command_failed");
    private final String notClan = config.getMessages("not_clan");
    private final String zeroCannotOutput = config.getMessages("zero_cannot_output");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection);
        PlayerRepository playerRepository = new PlayerRepository(connection);
        PlayerBalanceManager balanceManager = new PlayerBalanceManager();

        String clanName = SystemClans.getClanNameByPlayer().get(player);

        if (clanName != null){
            if (args.length == 1){
                int amount = Integer.parseInt(args[0]);
                int clanBalance = clanRepository.getClanBalance(clanName);
                if (balanceManager.getBalance(player) >= amount){
                    if (amount + clanBalance <= getMaxBalance(clanName)){
                        if (amount > 0){
                            clanRepository.setClanBalance(clanName, clanRepository.getClanBalance(clanName)+amount);
                            balanceManager.removeBalance(player, -amount);
                            playerRepository.addBalance(userName, amount);
                            sendMessageEveryone(clanName,  clanPay.replace("{userName}", player.getName()).replace("{amount}", String.valueOf(amount)), null);
                        }else {
                            player.sendMessage(color(zeroCannotOutput));
                        }
                    }else {
                        player.sendMessage(color(payFailed));
                    }
                }else {
                    player.sendMessage(color(youDontHaveMoney));
                }
            }else {
                player.sendMessage(color(commandFailed));
            }
        }else {
            player.sendMessage(color(notClan));
        }
        return true;
    }

    private int getMaxBalance(String clanName){
        ClanRepository clanRepository = new ClanRepository(SystemClans.getConnection());
        int level = clanRepository.getClanLevel(clanName);

        if (level == 1){
            return maxBalanceLevelOne;
        }

        if (level == 2){
            return maxBalanceLevelTwo;
        }

        if (level == 3){
            return maxBalanceLevelThree;
        }

        return maxBalanceLevelZero;
    }
}
