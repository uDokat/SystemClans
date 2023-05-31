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

public class ClanTakeSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForTake = config.getClanSettings("permission_for_take");
    private final String take = config.getMessages("clan_take");
    private final String takeFailed = config.getMessages("clan_take_failed");
    private final String commandFailed = config.getMessages("command_failed");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String notClan = config.getMessages("not_clan");
    private final String zeroCannotOutput = config.getMessages("zero_cannot_output");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection);
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = clanRepository.getClanName(userName);

        PlayerBalanceManager balanceManager = new PlayerBalanceManager();

        if (clanName != null){
            if (playerRepository.getPlayerGroup(userName) >= permissionForTake){
                if (args.length == 1){
                    int amount = Integer.parseInt(args[0]);
                    if (amount <= clanRepository.getClanBalance(clanName)){
                        if (amount > 0){
                            clanRepository.setClanBalance(clanName, clanRepository.getClanBalance(clanName)-amount);
                            balanceManager.addBalance(player, amount);
                            sendMessageEveryone(clanName, take.replace("{userName}", userName).replace("{amount}", String.valueOf(amount)), null);
                        }else {
                            player.sendMessage(color(zeroCannotOutput));
                        }
                    }else {
                        player.sendMessage(color(takeFailed));
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
