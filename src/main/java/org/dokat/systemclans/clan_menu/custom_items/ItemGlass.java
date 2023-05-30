package org.dokat.systemclans.clan_menu.custom_items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemGlass implements CustomItem{

    public ItemStack createItem(){
        return new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    }
}
