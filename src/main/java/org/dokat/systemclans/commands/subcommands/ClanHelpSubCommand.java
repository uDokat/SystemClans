package org.dokat.systemclans.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanHelpSubCommand implements SubCommand{

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;



        return true;
    }
}
