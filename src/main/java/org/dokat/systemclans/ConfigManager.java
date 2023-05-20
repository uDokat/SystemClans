package org.dokat.systemclans;

public class ConfigManager {

    public String getMessages(String nav){
        return SystemClans.getInstance().getConfig().getString("messages." + nav);
    }

    public int getClanSettings(String nav){
        return SystemClans.getInstance().getConfig().getInt("clan_settings." + nav);
    }
}
