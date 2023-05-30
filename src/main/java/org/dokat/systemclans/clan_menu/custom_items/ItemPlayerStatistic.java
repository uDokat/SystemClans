package org.dokat.systemclans.clan_menu.custom_items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.util.ArrayList;

public class ItemPlayerStatistic implements CustomItem{

    private final ArrayList<String> lore = new ArrayList<>();

    private final String mColor = config.getColorMenu("menu_color");
    private final String nameColor = config.getColorMenu("name_color");
    private final String timeColor = config.getColorMenu("time_color");
    private final String groupColor = config.getColorMenu("player_group_color");
    private final String killsColor = config.getColorMenu("clan_amount_kills_color");

    public ItemStack createItem(PlayerRepository playerRepository, Player player) {
        //Сделаешь голову со скином
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);

        String userName = player.getName();

        lore.add(color(mColor + "Ранг: " + groupColor + playerRepository.groupToString(playerRepository.getPlayerGroup(userName))));
        lore.add(color(mColor + "Убийств: " + killsColor + playerRepository.getAmountKills(userName)));
        lore.add(color(mColor + "Вступил: " + timeColor + playerRepository.getDateAdd(userName)));

        setLore(lore, itemStack, "Личная статистика", nameColor);
        return itemStack;
    }

}
