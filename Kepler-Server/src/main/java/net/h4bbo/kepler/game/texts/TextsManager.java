package net.h4bbo.kepler.game.texts;

import net.h4bbo.kepler.dao.mysql.SettingsDao;

import java.util.Map;

public class TextsManager {
    private static TextsManager instance;
    private Map<String, String> textsMap;

    public TextsManager() {
        this.textsMap = SettingsDao.getTexts();
    }

    /**
     * Get a external text value by key
     *
     * @param key the external text key to get
     * @return the external text value
     */
    public String getValue(String key) {
        return this.textsMap.getOrDefault(key, "");
    }

    /**
     * Reload the texts manager
     */
    public static void reset() {
        instance = null;
        TextsManager.getInstance();
    }

    /**
     * Get the {@link TextsManager} instance
     *
     * @return the item manager instance
     */
    public static TextsManager getInstance() {
        if (instance == null) {
            instance = new TextsManager();
        }

        return instance;
    }
}
