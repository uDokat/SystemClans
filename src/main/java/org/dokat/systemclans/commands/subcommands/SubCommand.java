package org.dokat.systemclans.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;

public interface SubCommand {
    boolean execute(CommandSender sender, String[] args);

    default String color(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
