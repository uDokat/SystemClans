package org.dokat.systemclans.clan_menu.custom_items;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;

import java.sql.Connection;
import java.util.ArrayList;

public interface CustomItem {

    ConfigManager config = new ConfigManager();

    default void setLore(ArrayList<String> lore, ItemStack item, String name, String color){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(color(color + name));

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }

    default String color(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
