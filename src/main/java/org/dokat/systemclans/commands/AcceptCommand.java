package org.dokat.systemclans.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.management.ClanInviteManager;
import org.jetbrains.annotations.NotNull;

public class AcceptCommand implements CommandExecutor{

    private ClanInviteManager clanInviteManager = SystemClans.getClanInviteManager();

    public AcceptCommand(){
        PluginCommand pluginCommand = SystemClans.getInstance().getCommand("accept");
        assert pluginCommand != null;
        pluginCommand.setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        if (args.length == 0){
            Bukkit.getScheduler().runTaskAsynchronously(SystemClans.getInstance(), () -> {
                clanInviteManager.acceptInvite(player);
            });
        }

        return true;
    }
}
