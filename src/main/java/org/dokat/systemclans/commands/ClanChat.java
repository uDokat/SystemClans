package org.dokat.systemclans.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.chat_managment.ClanChatManager;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.utils.Utility;
import org.jetbrains.annotations.NotNull;

public class ClanChat implements CommandExecutor, Utility {

    public ClanChat(){
        PluginCommand pluginCommand = SystemClans.getInstance().getCommand("clanchat");
        assert pluginCommand != null;
        pluginCommand.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        if (args.length > 0){
            String message = String.join(" ", args);

            ClanRepository clanRepository = new ClanRepository(SystemClans.getConnection(), "");
            ClanChatManager chatManager = new ClanChatManager();

            chatManager.sendClanChatMessage(clanRepository.getClanName(player.getName()), player, message);
        }

        return true;
    }
}
