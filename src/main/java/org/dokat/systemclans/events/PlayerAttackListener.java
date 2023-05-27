package org.dokat.systemclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.dokat.systemclans.SystemClans;

public class PlayerAttackListener implements Listener {

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player attackedPlayer = (Player) event.getEntity();
            Player attackingPlayer = (Player) event.getDamager();

            if (isClanMember(attackedPlayer, attackingPlayer)) {
                if (!isClanPvPEnabled(attackingPlayer)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public boolean isClanMember(Player attacked, Player attacking){
        String attackedClanName = "";
        if (SystemClans.getIsSameClan().get(attacked) != null){
            attackedClanName = SystemClans.getIsSameClan().get(attacked);
        }
        String attackingClanName = "";
        if (SystemClans.getIsSameClan().get(attacking) != null){
            attackingClanName = SystemClans.getIsSameClan().get(attacking);
        }

        return attackedClanName.equalsIgnoreCase(attackingClanName);
    }

    private boolean isClanPvPEnabled(Player attacked){
        return SystemClans.getStatusPvp().get(SystemClans.getIsSameClan().get(attacked));
    }
}
