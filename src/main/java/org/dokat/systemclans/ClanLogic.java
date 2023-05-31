package org.dokat.systemclans;

import org.bukkit.entity.Player;
import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;
import org.dokat.systemclans.utils.Utility;


public class ClanLogic implements Utility {

    private final ConfigManager config = new ConfigManager();
    private final int balanceToLevelUpOne = config.getClanSettings("balance_to_level_up_one");
    private final int balanceToLevelUpTwo = config.getClanSettings("balance_to_level_up_two");
    private final int balanceToLevelUpThree = config.getClanSettings("balance_to_level_up_three");

    private final int maxBalanceLevelOne = config.getClanSettings("max_balance_level_one");
    private final int maxBalanceLevelTwo = config.getClanSettings("max_balance_level_two");
    private final int maxBalanceLevelThree = config.getClanSettings("max_balance_level_three");

    private final String upLevelFailed = config.getMessages("clan_up_level_failed");
    private final String maxLevel = config.getMessages("clan_max_level");
    private final String upLevel = config.getMessages("clan_up_level");

    private final String clanName;
    private final ClanRepository clanRepository;
    private final Player player;

    public ClanLogic(String clanName, Player player, ClanRepository clanRepository){
        this.player = player;
        this.clanName = clanName;
        this.clanRepository = clanRepository;
    }

    public void levelUp(){
        int level = clanRepository.getClanLevel(clanName);
        int balance = clanRepository.getClanBalance(clanName);

        if (level < 3){
            if (level == 0){
                if (balance >= balanceToLevelUpOne){
                    clanRepository.setClanLevel(clanName);
                    clanRepository.setClanBalance(clanName, (balance - balanceToLevelUpThree));
                    sendMessageEveryone(clanName, upLevel, null);
                    return;
                }else {
                    player.sendMessage(color(upLevelFailed));
                }
            }

            if (level == 1){
                if (balance >= balanceToLevelUpTwo){
                    clanRepository.setClanLevel(clanName);
                    clanRepository.setClanBalance(clanName, (balance - balanceToLevelUpTwo));
                    sendMessageEveryone(clanName, upLevel, null);
                    return;
                }else {
                    player.sendMessage(color(upLevelFailed));
                }
            }

            if (level == 2){
                if (balance >= balanceToLevelUpThree){
                    clanRepository.setClanLevel(clanName);
                    clanRepository.setClanBalance(clanName, (balance - balanceToLevelUpThree));
                    sendMessageEveryone(clanName, upLevel, null);
                }else {
                    player.sendMessage(color(upLevelFailed));
                }
            }
        }else {
            player.sendMessage(color(maxLevel));
        }
    }

    public boolean isMaxBalance(){
        int level = clanRepository.getClanLevel(clanName);
        int balance = clanRepository.getClanBalance(clanName);


        if (level == 0){
            if (balance == maxBalanceLevelOne){
                return true;
            }
        }

        if (level == 1){
            if (balance == maxBalanceLevelTwo){
                return true;
            }
        }

        if (level == 2){
            if (balance == maxBalanceLevelThree){
                return true;
            }
        }

        return false;
    }
}
