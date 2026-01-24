package net.h4bbo.kepler.util.config;

import net.h4bbo.kepler.dao.mysql.SettingsDao;
import net.h4bbo.kepler.util.config.writer.ConfigWriter;

import java.util.Map;

public class GameConfiguration {
    private static GameConfiguration instance;
    private Map<String, String> config;

    public GameConfiguration(ConfigWriter configWriter) {
        this.config = configWriter.setConfigurationDefaults();

        Map<String, String> settings = SettingsDao.getAllSettings();

        for (var entrySet : this.config.entrySet()) {
            String value = settings.get(entrySet.getKey());

            if (value != null) {
                this.config.put(entrySet.getKey(), value);
            } else {
                SettingsDao.newSetting(entrySet.getKey(), entrySet.getValue());
            }
        }
    }

    /**
     * Get the configuration map.
     *
     * @return the configuration map
     */
    public Map<String, String> getConfig() {
        return config;
    }

    /**
     * Get key from configuration and cast to an Boolean
     *
     * @param key the key to use
     * @return value as boolean
     */
    public boolean getBoolean(String key) {
        String val = config.getOrDefault(key, "false");

        if (val.equalsIgnoreCase("true")) {
            return true;
        }

        if (val.equals("1")) {
            return true;
        }

        return val.equalsIgnoreCase("yes");

    }

    /**
     * Get value from configuration
     *
     * @param key the key to use
     * @return value
     */
    public String getString(String key) {
        return config.getOrDefault(key, key);
    }

    /**
     * Get value from configuration with default value
     *
     * @param key the key to use
     * @param def the default value
     * @return value
     */
    public String getString(String key, String def) {
        return config.getOrDefault(key, def);
    }

    /**
     * Get value from configuration and cast to an Integer
     *
     * @param key the key to use
     * @return value as int
     */
    public int getInteger(String key) {
        return Integer.parseInt(config.getOrDefault(key, "0"));
    }

    /**
     * Get value from configuration and cast to a long.
     *
     * @param key the key to use
     * @return value as long
     */
    public long getLong(String key) {
        return Long.parseLong(config.getOrDefault(key, "0"));
    }

    /**
     * Reset all game configuration values.
     */
    public static void reset(ConfigWriter configWriter) {
        instance = null;
        GameConfiguration.getInstance(configWriter);
    }

    /**
     * Get the instance of {@link GameConfiguration}
     *
     * @return the instance
     */
    public static GameConfiguration getInstance(ConfigWriter... configWriter) {
        if (instance == null || configWriter.length > 0) {
            instance = new GameConfiguration(configWriter[0]);
        }

        return instance;
    }

    /**
     * Get if key exists.
     *
     * @param key to check
     * @return true if exists
     */
    public boolean exists(String key) {
        return config.containsKey(key);
    }
}
