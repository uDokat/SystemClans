package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;

public class ClanCreateSubCommand implements SubCommand {

    @Override
    public boolean execute(CommandSender sender, String[] args){
        Player player = (Player) sender;
        String userName = player.getName();

        ClanRepository clanRepository = new ClanRepository(SystemClans.getConnection(), userName);
        ConfigManager config = new ConfigManager();

        String clanName = args[0].toUpperCase();

        //добавить проверку на название клана который игрок указывает

        if (args.length == 1){
            if (clanRepository.getClanStatus(userName) == null){
                if (clanRepository.isClanNameNotFound(clanName)){
                    clanRepository.createClan(clanName);
                    player.sendMessage(color(config.getMessages("clan_created").replace("{clanName}", clanName)));
                }else {
                    player.sendMessage(color(config.getMessages("clan_already_exists")));
                }
            }else {
                player.sendMessage(color(config.getMessages("you_already_in_clan")));
            }
        }

        return true;
    }
}
