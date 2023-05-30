package org.dokat.systemclans.clan_menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.clan_menu.custom_items.ItemNavigation;
import org.dokat.systemclans.clan_menu.custom_items.ItemPvp;
import org.jetbrains.annotations.NotNull;

public class SettingsMenu implements InventoryHolder {
    private Inventory inventory;

    public SettingsMenu(String clanName){
        this.inventory = SystemClans.getInstance().getServer().createInventory(this, 9, "Настройки");
        this.inventory.setItem(0, new ItemNavigation().createItem("Назад"));
        this.inventory.setItem(4, new ItemPvp().createItem(isPvpEnabled(clanName)));
    }

    private String isPvpEnabled(String clanName){
        if (SystemClans.getStatusPvp().get(clanName)){
            return "&aON";
        }else {
            return "&cOFF";
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
