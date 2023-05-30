package org.dokat.systemclans.clan_menu.custom_items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemClanSettings implements CustomItem{

    private final String nameColor = config.getColorMenu("name_color");

    public ItemStack createItem(){
        ItemStack itemStack = new ItemStack(Material.COMPARATOR);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(color(nameColor + "Настройки"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
