package org.dokat.systemclans.clan_menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.clan_menu.custom_items.*;
import org.dokat.systemclans.dbmanagement.repositories.*;

public class ClanMenu implements InventoryHolder {

    private final Inventory inventory;

    public ClanMenu(ClanRepository clanRepository, PlayerRepository playerRepository, Player player){
        this.inventory = SystemClans.getInstance().getServer().createInventory(this, 45, "Меню клана");

        ItemStack itemStack = new ItemGlass().createItem();

        inventory.setItem(3, new ItemClanInformation().createItem(player, clanRepository));
        inventory.setItem(5, new ItemPlayerStatistic().createItem(playerRepository, player));
        inventory.setItem(44, new ItemClanSettings().createItem());
        inventory.setItem(38, new ItemNavigation().createItem("Назад"));
        inventory.setItem(42, new ItemNavigation().createItem("Вперёд"));

        int[] positions = {0, 1, 2, 4, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 39, 40, 41, 43};
        for (int pos : positions){
            inventory.setItem(pos, itemStack);
        }
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
