package org.dokat.systemclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.clan_menu.ClanMenu;
import org.dokat.systemclans.clan_menu.SettingsMenu;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.sql.Connection;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void inventoryClickMenu(InventoryClickEvent event){
        if (event.getView().getTitle().equalsIgnoreCase("Меню клана")){
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ClanRepository clanRepository = new ClanRepository(SystemClans.getConnection(), "");

            if (event.getRawSlot() == 44){
                event.getWhoClicked().openInventory(new SettingsMenu(clanRepository.getClanName(player.getName())).getInventory());
            }
        }
    }

    @EventHandler
    public void inventoryClickSettings(InventoryClickEvent event){
        if (event.getView().getTitle().equalsIgnoreCase("Настройки")){
            event.setCancelled(true);

            if (event.getRawSlot() == 0){
                Connection connection = SystemClans.getConnection();
                ClanRepository clanRepository = new ClanRepository(connection, "");
                PlayerRepository playerRepository = new PlayerRepository(connection);

                event.getWhoClicked().openInventory(new ClanMenu(clanRepository, playerRepository, (Player) event.getWhoClicked()).getInventory());
            }

            if (event.getRawSlot() == 4){
                Connection connection = SystemClans.getConnection();
                ClanRepository clanRepository = new ClanRepository(connection, "");

                Player player = (Player) event.getWhoClicked();
                String userName = player.getName();
                String clanName = clanRepository.getClanName(userName);

                clanRepository.setStatusPvp(clanRepository.getClanName(userName), !clanRepository.getStatusPvp(clanName));

                player.openInventory(new SettingsMenu(clanName).getInventory());
            }
        }
    }
}
