package org.dokat.systemclans.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.clanmenu.ClanMenu;
import org.dokat.systemclans.commands.subcommands.*;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClanCommand implements CommandExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public ClanCommand(){
        PluginCommand pluginCommand = SystemClans.getInstance().getCommand("clan");
        assert pluginCommand != null;
        pluginCommand.setExecutor(this);

        subCommands.put("create", new ClanCreateSubCommand());
        subCommands.put("add", new ClanAddSubCommand());
        subCommands.put("kick", new ClanKickSubCommand());
        subCommands.put("delete", new ClanDeleteSubCommand());
        subCommands.put("rename", new ClanRenameSubCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String userName = player.getName();

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, userName);

        ClanMenu clanMenu = new ClanMenu(userName);
        ConfigManager config = new ConfigManager();

        if (args.length == 0){
            if (clanRepository.getClanStatus(userName) != null){
                clanMenu.setItem();
                player.openInventory(clanMenu.getInventory());
                return true;
            }else {
                player.sendMessage(color(config.getMessages("not_clan")));
                return true;
            }
        }

        String subCommandName = args[0];
        SubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand == null) {
            player.sendMessage(color( config.getMessages("subcommand_not_found")));
            return true;
        }

        return subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    private String color(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}