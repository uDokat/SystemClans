package org.dokat.systemclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.sql.Connection;


/**
 * Класс PlayerDeathListener обрабатывает события смерти игрока.
 */
public class PlayerDeathListener implements Listener {

    private final Connection connection = SystemClans.getConnection();
    private final ClanRepository clanRepository = new ClanRepository(connection);
    private final PlayerRepository playerRepository = new PlayerRepository(connection);

    /**
     * Обрабатывает событие смерти игрока.
     *
     * @param event событие смерти игрока
     */
    @EventHandler
    public void playerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        Player killer = player.getKiller();

        // Проверяет, является ли убийца и жертва членами одного клана
        if (killer != null && !isClanMember(player, killer)){
            String killerName = killer.getName();

            // Увеличивает количество убийств для клана убийцы
            clanRepository.addAmountkills(clanRepository.getClanName(killerName));
            // Увеличивает количество убийств для игрока-убийцы
            playerRepository.addAmountKills(killerName);
        }
    }

    /**
     * Проверяет, являются ли игроки членами одного клана.
     *
     * @param attacked   атакуемый игрок
     * @param attacking  атакующий игрок
     * @return true, если игроки члены одного клана, иначе false
     */
    public boolean isClanMember(Player attacked, Player attacking){
        String attackedClanName = "";
        if (SystemClans.getClanNameByPlayer().get(attacked) != null){
            attackedClanName = SystemClans.getClanNameByPlayer().get(attacked);
        }
        String attackingClanName = "";
        if (SystemClans.getClanNameByPlayer().get(attacking) != null){
            attackingClanName = SystemClans.getClanNameByPlayer().get(attacking);
        }

        return attackedClanName.equalsIgnoreCase(attackingClanName);
    }
}
