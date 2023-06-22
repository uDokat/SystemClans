package org.dokat.systemclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.clan_menu.ClanMenu;
import org.dokat.systemclans.clan_menu.PageLogic;
import org.dokat.systemclans.clan_menu.PageManager;
import org.dokat.systemclans.clan_menu.SettingsMenu;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;
import org.dokat.systemclans.utils.Utility;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс InventoryClickListener обрабатывает события клика по инвентарю.
 */
public class InventoryClickListener implements Listener, Utility {

    private final ConfigManager config = new ConfigManager();
    private final int permissionForSetPvp = config.getClanSettings("permission_for_set_pvp");
    private final String lackOfRights = config.getMessages("lack_of_rights");

    private final HashMap<Player, PageManager> pageManagerMap = new HashMap<>();

    public InventoryClickListener(){
        SystemClans.getInstance().getServer().getPluginManager().registerEvents(this, SystemClans.getInstance());
    }

    /**
     * Обрабатывает событие клика по меню клана.
     *
     * @param event событие клика по инвентарю
     */
    @EventHandler
    public void inventoryClickMenu(InventoryClickEvent event){
        if (event.getView().getTitle().equalsIgnoreCase("Меню клана")){
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            Connection connection = SystemClans.getConnection();
            ClanRepository clanRepository = new ClanRepository(connection);

            if (event.getRawSlot() == 44){
                // Открывает инвентарь настроек клана
                event.getWhoClicked().openInventory(new SettingsMenu(clanRepository.getClanName(player.getName())).getInventory());
                return;
            }


            if (event.getRawSlot() == 42 && pageManagerMap.get(player) != null){
                PageManager pageManager = pageManagerMap.get(player);
                ArrayList<Inventory> inventories = pageManager.getInventories();

                int page = pageManager.nextPage();
                if (inventories.get(page) != null){
                    event.getWhoClicked().openInventory(inventories.get(page));
                }
            }

            if (event.getRawSlot() == 38){
                PageManager pageManager = pageManagerMap.get(player);
                ArrayList<Inventory> inventories = pageManager.getInventories();

                int page = pageManager.prevPage();
                if (inventories.get(page) != null && pageManagerMap.get(player) != null){
                    event.getWhoClicked().openInventory(inventories.get(page));
                }
            }
        }
    }

    /**
     * Обрабатывает событие клика по настройкам клана.
     *
     * @param event событие клика по инвентарю
     */
    @EventHandler
    public void inventoryClickSettings(InventoryClickEvent event){
        if (event.getView().getTitle().equalsIgnoreCase("Настройки")){
            event.setCancelled(true);

            if (event.getRawSlot() == 0){
                // Открывает инвентарь меню клана
                Player player = (Player) event.getWhoClicked();
                String userName = player.getName();

                Connection connection = SystemClans.getConnection();
                ClanRepository clanRepository = new ClanRepository(connection);
                PlayerRepository playerRepository = new PlayerRepository(connection);

                event.getWhoClicked().openInventory(new ClanMenu(clanRepository, playerRepository, player).getInventory());
            }

            if (event.getRawSlot() == 4){
                Player player = (Player) event.getWhoClicked();
                String userName = player.getName();

                Connection connection = SystemClans.getConnection();
                PlayerRepository playerRepository = new PlayerRepository(connection);

                // Изменяет статус PvP клана если у игрока есть соответствующее разрешение
                if (playerRepository.getPlayerGroup(userName) >= permissionForSetPvp){
                    ClanRepository clanRepository = new ClanRepository(connection);
                    String clanName = clanRepository.getClanName(userName);

                    clanRepository.setStatusPvp(clanRepository.getClanName(userName), !clanRepository.getStatusPvp(clanName));
                    player.openInventory(new SettingsMenu(clanName).getInventory());
                }else {
                    player.sendMessage(color(lackOfRights));
                }
            }
        }
    }

    @EventHandler
    public void  inventoryOpen(InventoryOpenEvent event){
        Player player = (Player) event.getPlayer();
        String userName = player.getName();

        if (event.getView().getTitle().equalsIgnoreCase("Меню клана")){
            Connection connection = SystemClans.getConnection();
            ClanRepository clanRepository = new ClanRepository(connection);
            PlayerRepository playerRepository = new PlayerRepository(connection);

            ClanMenu clanMenu = new ClanMenu(clanRepository, playerRepository, player);
            PageLogic pageLogic = new PageLogic(clanMenu.getInventory(), clanRepository.getClanName(userName));
            PageManager pageManager = new PageManager(pageLogic);
            pageManagerMap.put(player, pageManager);
        }
    }

    @EventHandler
    public void  inventoryOpen(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();

        if (event.getView().getTitle().equalsIgnoreCase("Меню клана")){
            pageManagerMap.remove(player);
        }
    }
}
