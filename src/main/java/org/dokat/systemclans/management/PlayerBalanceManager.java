package org.dokat.systemclans.management;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PlayerBalanceManager {

    private Economy economy;

    public PlayerBalanceManager(){
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null){
            economy = rsp.getProvider();
        }
    }

    public void removeBalance(Player player, int amount){
        if (economy != null){
            economy.withdrawPlayer(player, Math.abs(amount));
        }
    }

    public void addBalance(Player player, int amount){
        if (economy != null){
            economy.depositPlayer(player, amount);
        }
    }

    public int getBalance(Player player){
        if (economy != null){
           return (int) economy.getBalance(player);
        }else {
            return 0;
        }
    }
}
