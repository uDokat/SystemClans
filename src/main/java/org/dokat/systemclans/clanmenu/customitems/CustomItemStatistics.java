package org.dokat.systemclans.clanmenu.customitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class CustomItemStatistics implements CustomItem {

    private static List<String> lore = new ArrayList<>();

    @Override
    public ItemStack createItem(String userName) {
        ItemStack itemStack = new ItemStack(Material.DIAMOND);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(color("&c" + "Статистика"));

        Connection connection = SystemClans.getConnection();
        ClanRepository clanRepository = new ClanRepository(connection, userName);
        PlayerRepository playerRepository = new PlayerRepository(connection);

        String clanName = clanRepository.getClanStatus(userName);

        lore.add(color("&a" + "Название: " + "&6&l" + clanName));
        lore.add(color("&a" + "Ранг: " + "&c" + playerRepository.groupToString(playerRepository.getPlayerGroup(userName))));
        lore.add(color("&a" + "Уровень: " + "&c" + clanRepository.getClanLevel(clanName)));
        lore.add(color("&a" + "Игроков в клане: " + "&6" + clanRepository.getAmountPlayer(clanName)));
        lore.add(color("&a" + "Казна: " + "&6" + clanRepository.getClanBalance()));

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        lore.clear();
        return itemStack;
    }
}
