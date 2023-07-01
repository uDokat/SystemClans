package org.dokat.systemclans.dbmanagement.data_models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Player {

    private String userName;
    private String clanName;
    private int group = 0;
    private int kills = 0;
    private long contribute = 0;
    private String dateAdd;

    public Player(String userName, String clanName) {
        this.userName = userName;
        this.clanName = clanName;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
        LocalDateTime localDateTime = LocalDateTime.now();
        this.dateAdd = localDateTime.format(formatter);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public long getContribute() {
        return contribute;
    }

    public void setContribute(long contribute) {
        this.contribute = contribute;
    }

    public String getDateAdd() {
        return dateAdd;
    }

    public void setDateAdd(String dateAdd) {
        this.dateAdd = dateAdd;
    }
}
