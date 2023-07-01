package org.dokat.systemclans.dbmanagement.controllers;

import org.dokat.systemclans.dbmanagement.data_models.Clan;
import org.dokat.systemclans.dbmanagement.data_models.Player;

import java.util.ArrayList;

public class DataController {

    private static final ArrayList<Clan> CLANS = new ArrayList<>();
    private static final ArrayList<Player> PLAYERS = new ArrayList<>();

    public static ArrayList<Clan> getClans(){
        return CLANS;
    }

    public static Clan getClan(String clanName){
        for (Clan clan : CLANS){
            if (clan.getName().equals(clanName)) return clan;
        }

        return null;
    }

    public static void addClan(Clan clan){
        if (!CLANS.contains(clan)){
            CLANS.add(clan);
        }
    }

    public static ArrayList<Player> getPlayers(){
        return PLAYERS;
    }

    public static Player getPlayer(String userName){
        for (Player player : PLAYERS){
            if (player.getUserName().equals(userName)) return player;
        }

        return null;
    }

    public static void addPlayer(Player player){
        if (!PLAYERS.contains(player)){
            PLAYERS.add(player);
        }
    }
}
