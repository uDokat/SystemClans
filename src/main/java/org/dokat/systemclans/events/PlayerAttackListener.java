package org.dokat.systemclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.dokat.systemclans.SystemClans;

/**
 * Обрабатывает событие атаки игрока по другому игроку.
 */
public class PlayerAttackListener implements Listener {

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attackedPlayer = (Player) event.getEntity();
            Player attackingPlayer = (Player) event.getDamager();

            // Проверяет, являются ли игроки членами одного клана
            if (isClanMember(attackedPlayer, attackingPlayer)) {
                // Проверяет, разрешен ли PvP в клане атакующего игрока
                if (!isClanPvPEnabled(attackingPlayer)) {
                    event.setCancelled(true);
                }
            }
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

    /**
     * Проверяет, разрешен ли PvP в клане игрока.
     *
     * @param attacked  атакуемый игрок
     * @return true, если PvP разрешен, иначе false
     */
    private boolean isClanPvPEnabled(Player attacked){
        return SystemClans.getStatusPvp().get(SystemClans.getClanNameByPlayer().get(attacked));
    }
}
