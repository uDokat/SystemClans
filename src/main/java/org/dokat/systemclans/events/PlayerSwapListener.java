package org.dokat.systemclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.InventoryView;

public class PlayerSwapListener implements Listener {

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        InventoryView openInventory = player.getOpenInventory();
        if (openInventory != null && openInventory.getTitle().equalsIgnoreCase("Меню клана")) {
            event.setCancelled(true);
            player.updateInventory();
        }

        InventoryView openInventory1 = player.getOpenInventory();
        if (openInventory1 != null && openInventory1.getTitle().equalsIgnoreCase("Настройки")) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}
