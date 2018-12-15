package org.alexdev.kepler.util.config;

import org.alexdev.kepler.dao.mysql.SettingsDao;

import java.util.LinkedHashMap;
import java.util.Map;

public class GameConfiguration {
    private static GameConfiguration instance;
    private Map<String, String> config;

    public GameConfiguration() {
        this.config = new LinkedHashMap<>();
        this.setConfigurationDefaults();

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

    private void setConfigurationDefaults() {
        config.put("fuck.aaron", "true");
        config.put("max.connections.per.ip", "2");

        config.put("normalise.input.strings", "false");

        config.put("welcome.message.enabled", "false");
        config.put("welcome.message.content", "Hello, %username%! And welcome to the Kepler server!");

        config.put("roller.tick.default", "2000");

        config.put("afk.timer.seconds", "900");
        config.put("sleep.timer.seconds", "300");
        config.put("carry.timer.seconds", "300");

        config.put("stack.height.limit", "8");
        config.put("roomdimmer.scripting.allowed", "false");

        config.put("credits.scheduler.timeunit", "MINUTES");
        config.put("credits.scheduler.interval", "15");
        config.put("credits.scheduler.amount", "20");

        config.put("chat.garbled.text", "true");
        config.put("chat.bubble.timeout.seconds", "15");

        config.put("messenger.max.friends.nonclub", "100");
        config.put("messenger.max.friends.club", "600");

        config.put("battleball.create.game.enabled", "true");
        config.put("battleball.start.minimum.active.teams", "2");
        config.put("battleball.preparing.game.seconds", "10");
        config.put("battleball.game.lifetime.seconds", "180");
        config.put("battleball.restart.game.seconds", "30");
        config.put("battleball.ticket.charge", "2");
        config.put("battleball.increase.points", "true");

        config.put("game.finished.listing.expiry.seconds", "300");

        config.put("snowstorm.create.game.enabled", "false");
        config.put("snowstorm.start.minimum.active.teams", "2");
        config.put("snowstorm.preparing.game.seconds", "10");
        //config.put("snowstorm.game.lifetime.seconds", "180");
        config.put("snowstorm.restart.game.seconds", "30");
        config.put("snowstorm.ticket.charge", "2");
        config.put("snowstorm.increase.points", "true");

        config.put("tutorial.enabled", "true");
        config.put("profile.editing", "true");
        config.put("vouchers.enabled", "true");

        config.put("shutdown.minutes", "1");

        config.put("reset.sso.after.login", "true");
        config.put("navigator.show.hidden.rooms", "false");

        config.put("rare.cycle.tick.time", "0");
        config.put("rare.cycle.page.id", "2");
        config.put("rare.cycle.refresh.timeunit", "DAYS");
        config.put("rare.cycle.refresh.interval", "1");

        config.put("rare.cycle.reuse.timeunit", "DAYS");
        config.put("rare.cycle.reuse.interval", "3");

        config.put("club.gift.timeunit", "DAYS");
        config.put("club.gift.interval", "7");
        config.put("club.gift.present.label", "From Habbo");

        // Catalogue pages for rare items, delimetered by pipe, first integer is page ID and second number is the amount of hours required for that rare to be affordable
        config.put("rare.cycle.pages","28,3|29,3|31,3|32,3|33,3|34,3|35,3|36,3|40,3|43,3|30,6|37,6|38,6|39,6|44,6");
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
    public static void reset() {
        instance = null;
        GameConfiguration.getInstance();
    }

    /**
     * Get the instance of {@link GameConfiguration}
     *
     * @return the instance
     */
    public static GameConfiguration getInstance() {
        if (instance == null) {
            instance = new GameConfiguration();
        }

        return instance;
    }
}
