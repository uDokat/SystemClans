package org.dokat.systemclans.clan_menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.clan_menu.custom_items.CustomItemStatistics;
import org.jetbrains.annotations.NotNull;

public class ClanMenu implements InventoryHolder {

    private final Inventory inventory;
    private String userName;

    public ClanMenu(String userName){
        this.userName = userName;
        this.inventory = SystemClans.getInstance().getServer().createInventory(this, 45);
    }

    public void setItem(){
        CustomItemStatistics statistics = new CustomItemStatistics();
        inventory.setItem(22, statistics.createItem(userName));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
