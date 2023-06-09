package org.dokat.systemclans;

public class ConfigManager {

    /**
     * Возвращает сообщение из конфигурационного файла, соответствующее указанному ключу навигации.
     *
     * @param nav ключ навигации, указывающий на требуемое сообщение
     * @return сообщение из конфигурации
     */
    public String getMessages(String nav){
        return SystemClans.getInstance().getConfig().getString("messages." + nav);
    }

    /**
     * Возвращает значение настроек клана из конфигурационного файла, соответствующее указанному ключу навигации.
     *
     * @param nav ключ навигации, указывающий на требуемое значение настроек
     * @return значение настроек клана
     */
    public int getClanSettings(String nav){
        return SystemClans.getInstance().getConfig().getInt("clan_settings." + nav);
    }

    /**
     * Возвращает цвет из конфигурационного файла для меню, соответствующий указанному ключу навигации.
     *
     * @param nav ключ навигации, указывающий на требуемый цвет меню
     * @return цвет меню из конфигурации
     */
    public String getColorMenu(String nav){
        return SystemClans.getInstance().getConfig().getString("color_settings.menu." + nav);
    }
}
