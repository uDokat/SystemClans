package org.dokat.systemclans.dbmanagement.data_models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Clan {

    private int id;
    private String name;
    private int level = 0;
    private long balance = 0;
    private int amountPlayers = 1;
    private String welcomeMessage = null;
    private boolean pvpStatus = false;
    private int kills = 0;
    private int reputation = 0;
    private String dateCreate;
    private ClanHome clanHome = null;

    private final ArrayList<Player> players = new ArrayList<>();

    public Clan(String name) {
        this.name = name;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
        LocalDateTime localDateTime = LocalDateTime.now();
        this.dateCreate = localDateTime.format(formatter);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public int getAmountPlayers() {
        return amountPlayers;
    }

    public void setAmountPlayers(int amountPlayers) {
        this.amountPlayers = amountPlayers;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public boolean isPvpStatus() {
        return pvpStatus;
    }

    public void setPvpStatus(boolean pvpStatus) {
        this.pvpStatus = pvpStatus;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public ClanHome getClanHome() {
        return clanHome;
    }

    public void setClanHome(ClanHome clanHome) {
        this.clanHome = clanHome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
