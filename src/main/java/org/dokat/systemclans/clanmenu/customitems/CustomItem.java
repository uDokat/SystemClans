package org.dokat.systemclans.clanmenu.customitems;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public interface CustomItem {

    ItemStack createItem(String userName);

    default String color(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
