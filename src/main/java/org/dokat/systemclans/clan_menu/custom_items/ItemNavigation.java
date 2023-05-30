package org.dokat.systemclans.clan_menu.custom_items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemNavigation implements CustomItem{

    private final String navColor = config.getColorMenu("navigation_color");

    public ItemStack createItem(String name){
        ItemStack itemStack = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(color(navColor + name));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
