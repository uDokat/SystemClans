package org.dokat.systemclans.clan_menu;

import combatlib.utils.HeadUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;
import org.dokat.systemclans.utils.Utility;

import java.util.ArrayList;

public class PageLogic implements Utility {

    private final Inventory mainPage;
    private final String clanName;
    private final ArrayList<Inventory> inventories = new ArrayList<>();

    private final ConfigManager config = new ConfigManager();
    private final String cMenu = config.getColorMenu("menu_color");
    private final String cTime = config.getColorMenu("time_color");
    private final String cKill = config.getColorMenu("clan_amount_kills_color");
    private final String cBall = config.getColorMenu("clan_balance_color");
    private final String cGroup = config.getColorMenu("player_group_color");

    public PageLogic(Inventory mainPage, String clanName) {
        this.mainPage = mainPage;
        this.clanName = clanName;
        createPages();
    }

    public ArrayList<Inventory> getInventories() {
        return inventories;
    }

    private void createPages(){
        PlayerRepository pRepository =  new PlayerRepository(SystemClans.getConnection());
        ArrayList<String> players = pRepository.getAllPlayersForClanName(clanName);
        setHeads(mainPage, players);
    }

    private ItemStack getHead(String userName){
        ItemStack item = HeadUtil.getHeadByName(userName);
        setHeadMeta(item, userName);
        return item;
    }

    private void setHeads(Inventory inventory, ArrayList<String> names) {
        Inventory inv = Bukkit.createInventory(null, inventory.getSize(), "Меню клана");
        inv.setContents(mainPage.getContents().clone());

        for (String userName : names){
            if (inv.getItem(34) == null){
                inv.addItem(getHead(userName));
            }else {
                inventories.add(inv);
                inv = Bukkit.createInventory(null, mainPage.getSize(), "Меню клана");
                inv.setContents(mainPage.getContents().clone());
                inv.addItem(getHead(userName));
            }
        }

        inventories.add(inv);
    }

    private void setHeadMeta(ItemStack item, String userName){
        ArrayList<String> lore = new ArrayList<>();

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setDisplayName(color(cMenu + "Статистика " + userName));

        PlayerRepository pRepository = new PlayerRepository(SystemClans.getConnection());

        lore.add("");
        lore.add(color(cMenu + "Внёс: " + cBall + pRepository.getBalance(userName) + "$"));
        lore.add(color(cMenu + "Убийств: " + cKill + pRepository.getAmountKills(userName)));
        lore.add(color(cMenu + "Группа: " + cGroup + pRepository.groupToString(pRepository.getPlayerGroup(userName))));
        lore.add(color(cMenu + "Вступил: " + cTime + pRepository.getDateAdd(userName)));
        lore.add(color(cMenu + "Статус: " + isOnline(userName)));

        skullMeta.setLore(lore);
        item.setItemMeta(skullMeta);
    }

    private String isOnline(String userName){
        OfflinePlayer player = Bukkit.getOfflinePlayer(userName);
        if (player.isOnline()){
            return config.getColorMenu("online_color") + "Онлайн";
        }else {
            return config.getColorMenu("offline_color") + "Оффлайн";
        }
    }
}
