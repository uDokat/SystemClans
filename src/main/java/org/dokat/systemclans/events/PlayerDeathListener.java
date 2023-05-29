package org.dokat.systemclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.dbmanagement.repositories.PlayerRepository;

import java.sql.Connection;

public class PlayerDeathListener implements Listener {

    private final Connection connection = SystemClans.getConnection();
    private final ClanRepository clanRepository = new ClanRepository(connection, "");
    private final PlayerRepository playerRepository = new PlayerRepository(connection);

    @EventHandler
    public void playerDeath(PlayerDeathEvent event){
        Player player = event.getPlayer();
        Player killer = player.getKiller();

        if (killer != null && !isClanMember(player, killer)){
            String killerName = killer.getName();

            clanRepository.addAmountKillings(clanRepository.getClanName(killerName));
            playerRepository.addAmountKills(killerName);
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
}
