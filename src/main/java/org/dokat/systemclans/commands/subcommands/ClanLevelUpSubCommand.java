package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ClanLogic;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;
import org.dokat.systemclans.utils.Utility;

import java.sql.Connection;

public class ClanLevelUpSubCommand implements SubCommand,Utility {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForUpLevel = config.getClanSettings("permission_for_up_level");
    private final String lackOfRights = config.getMessages("lack_of_rights");
    private final String notClan = config.getMessages("not_clan");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection);
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = clanRepository.getClanName(userName);
        ClanLogic clanLogic = new ClanLogic(clanName, player, clanRepository);

        if (clanName != null){
            if (playerRepository.getPlayerGroup(userName) >= permissionForUpLevel){
                clanLogic.levelUp();
            }else {
                player.sendMessage(color(lackOfRights));
            }
        }else {
            player.sendMessage(color(notClan));
        }

        return true;
    }
}
