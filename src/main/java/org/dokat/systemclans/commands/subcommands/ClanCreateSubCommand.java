package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;

public class ClanCreateSubCommand implements SubCommand {

    private final ConfigManager config = new ConfigManager();
    private final String clanNameFailed = config.getMessages("clan_name_failed");
    private final String youAlreadyInClan = config.getMessages("you_already_in_clan");

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
                if (clanName.length() == 3 && clanRepository.isClanNameNotFound(clanName)){
                    clanRepository.createClan(clanName);
                    player.sendMessage(color(config.getMessages("clan_created").replace("{clanName}", clanName)));
                }else {
                    player.sendMessage(color(clanNameFailed));
                }
            }else {
                player.sendMessage(color(youAlreadyInClan));
            }
        }

        return true;
    }
}
