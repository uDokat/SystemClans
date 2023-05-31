package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.utils.Utility;

public class ClanHelpSubCommand implements SubCommand, Utility {

    private final ConfigManager config = new ConfigManager();
    private final String help = config.getMessages("command_help");
    private final String failed = config.getMessages("command_failed");

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0){
            player.sendMessage(color(help));
        }else {
            player.sendMessage(color(failed));
        }

        return true;
    }
}
