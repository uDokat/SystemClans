package org.dokat.systemclans.clan_menu.custom_items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemPvp implements CustomItem{

    private final ArrayList<String> lore = new ArrayList<>();

    private final String nameColor = config.getColorMenu("name_color");
    private final String mColor = config.getColorMenu("menu_color");

    public ItemStack createItem(String statusPvp){
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(color(nameColor + "Пвп"));

        lore.add(color(mColor + "Статус пвп: " + statusPvp));

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
