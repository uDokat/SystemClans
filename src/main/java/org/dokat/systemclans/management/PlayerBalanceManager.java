package org.dokat.systemclans.management;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Класс PlayerBalanceManager управляет балансом игроков.
 */
public class PlayerBalanceManager {

    private Economy economy;

    /**
     * Конструктор класса PlayerBalanceManager.
     * Инициализирует экономику плагина.
     */
    public PlayerBalanceManager(){
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null){
            economy = rsp.getProvider();
        }
    }

    /**
     * Уменьшает баланс игрока на указанную сумму.
     *
     * @param player игрок, у которого уменьшается баланс
     * @param amount сумма, на которую уменьшается баланс
     */
    public void removeBalance(Player player, int amount){
        if (economy != null){
            economy.withdrawPlayer(player, Math.abs(amount));
        }
    }

    /**
     * Увеличивает баланс игрока на указанную сумму.
     *
     * @param player игрок, у которого увеличивается баланс
     * @param amount сумма, на которую увеличивается баланс
     */
    public void addBalance(Player player, int amount){
        if (economy != null){
            economy.depositPlayer(player, amount);
        }
    }

    /**
     * Получает текущий баланс игрока.
     *
     * @param player игрок, чей баланс запрашивается
     * @return текущий баланс игрока
     */
    public int getBalance(Player player){
        if (economy != null){
           return (int) economy.getBalance(player);
        }else {
            return 0;
        }
    }
}
