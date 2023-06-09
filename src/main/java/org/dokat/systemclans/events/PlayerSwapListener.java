package org.dokat.systemclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.InventoryView;

/**
 * Класс PlayerSwapListener Обрабатывает событие свапа предметама игроком.
 */
public class PlayerSwapListener implements Listener {

    /**
     * Обрабатывает событие свапа предметама игроком.
     *
     * @param event событие обмена предметами
     */
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        InventoryView openInventory = player.getOpenInventory();
        // Если игрок имеет открытое инвентарное меню клана, отменяет событие и обновляет инвентарь игрока
        if (openInventory != null && openInventory.getTitle().equalsIgnoreCase("Меню клана")) {
            event.setCancelled(true);
            player.updateInventory();
        }

        InventoryView openInventory1 = player.getOpenInventory();
        // Если игрок имеет открытое инвентарное меню настроек, отменяет событие и обновляет инвентарь игрока
        if (openInventory1 != null && openInventory1.getTitle().equalsIgnoreCase("Настройки")) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}
