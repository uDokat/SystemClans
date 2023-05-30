package org.dokat.systemclans.clan_menu.custom_items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;

import java.util.ArrayList;

public class ItemClanInformation implements CustomItem {

    private final ArrayList<String> lore = new ArrayList<>();

    private final String mColor = config.getColorMenu("menu_color");
    private final String nameColor = config.getColorMenu("name_color");
    private final String timeColor = config.getColorMenu("time_color");
    private final String cNameColor = config.getColorMenu("clan_name_color");
    private final String repColor = config.getColorMenu("clan_rep_color");
    private final String levelColor = config.getColorMenu("clan_level_color");
    private final String playerColor = config.getColorMenu("clan_amount_player_color");
    private final String killsColor = config.getColorMenu("clan_amount_kills_color");
    private final String balanceColor = config.getColorMenu("clan_balance_color");

    public ItemStack createItem(Player player, ClanRepository clanRepository) {
        ItemStack itemStack = new ItemStack(Material.DIAMOND);
        String clanName = clanRepository.getClanName(player.getName());

        lore.add(color(mColor + "Имя клана: " + cNameColor + clanName));
        lore.add(color(mColor + "Уровень: " + levelColor + clanRepository.getClanLevel(clanName)));
        lore.add(color(mColor + "Репутация: " + repColor + clanRepository.getReputation(clanName)));
        lore.add(color(mColor + "Игроков в клане: " + playerColor + clanRepository.getAmountPlayer(clanName)));
        lore.add(color(mColor + "Убийств: " + killsColor + clanRepository.getAmountKillings(clanName)));
        lore.add(color(mColor + "Казна: " + balanceColor + clanRepository.getClanBalance()));
        lore.add(color(mColor + "Создан: " + timeColor + clanRepository.getDateCreate(clanName)));

        setLore(lore, itemStack, "Информация", nameColor);
        lore.clear();
        return itemStack;
    }
}
