package org.dokat.systemclans.clan_menu;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public class PageManager {

    private ArrayList<Inventory> inventories;

    private int page = 0;

    public PageManager(PageLogic pageLogic) {
        inventories = pageLogic.getInventories();
    }

    public ArrayList<Inventory> getInventories(){
        return inventories;
    }

    public int nextPage(){
        if (inventories.size() >= page){
            page++;
        }
        return page;
    }

    public int prevPage(){
        if (page > 0){
            page--;
        }
        return page;
    }
}
