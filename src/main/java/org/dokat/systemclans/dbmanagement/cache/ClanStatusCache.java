package org.dokat.systemclans.dbmanagement.cache;

import org.dokat.systemclans.dbmanagement.repositories.ClanRepository;

import java.sql.Connection;
import java.util.HashMap;

public class ClanStatusCache {

    private Connection connection;
    private HashMap<String, String> cache;

    public ClanStatusCache(Connection connection, HashMap<String, String> cache) {
        this.connection = connection;
        this.cache = cache;
    }

    public void setClanStatus(String userName, String clanName) {
        cache.put(userName, clanName);
    }

    public String getClanName(String userName){
        if (cache.containsKey(userName)) {
            return cache.get(userName);
        } else {
            ClanRepository repository = new ClanRepository(connection, userName);
            String clanStatus = repository.getClanName(userName);
            cache.put(userName, clanStatus);

            return cache.get(userName);
        }
    }

    public void deletePlayerFromCache(String userName){
        cache.remove(userName);
    }

    public HashMap<String, String> getCache() {
        return cache;
    }
}
