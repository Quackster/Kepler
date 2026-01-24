package net.h4bbo.kepler.game.player.statistics;

import net.h4bbo.kepler.dao.mysql.PlayerStatisticsDao;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatisticManager {
    private final int userId;
    private Map<PlayerStatistic, String> values;

    public PlayerStatisticManager(int userId, Map<PlayerStatistic, String> values) {
        this.userId = userId;
        this.values = values;
    }

    /**
     * Reload user statistics.
     */
    public void reload() {
        this.values = PlayerStatisticsDao.getStatistics(this.userId);
    }

    /**
     * Get the int value by player statistic key
     * @param playerStatistic the key
     * @return the value
     */
    public int getIntValue(PlayerStatistic playerStatistic) {
        if (this.values.containsKey(playerStatistic)) {
            return Integer.parseInt(this.values.get(playerStatistic));
        }

        return 0;
    }

    /**
     * Get the long value by player statistic key
     * @param playerStatistic the key
     * @return the value
     */
    public long getLongValue(PlayerStatistic playerStatistic) {
        if (this.values.containsKey(playerStatistic)) {
            return Long.parseLong(this.values.get(playerStatistic));
        }

        return 0;
    }

    /**
     * Set the value of the statistic.
     *
     * @param statistic the statistic
     * @param value the value
     */
    public void setLongValue(PlayerStatistic statistic, long value) {
        if (!this.values.containsKey(statistic)) {
            return;
        }

        this.values.put(statistic, String.valueOf(value));
        PlayerStatisticsDao.updateStatistic(this.userId, statistic, this.values.get(statistic));
    }

    /**
     * Set the value of the statistic.
     *
     * @param statistic the statistic
     * @param value the value
     */
    public void setValue(PlayerStatistic statistic, String value) {
        if (!this.values.containsKey(statistic)) {
            return;
        }

        this.values.put(statistic, value);
        PlayerStatisticsDao.updateStatistic(this.userId, statistic, this.values.get(statistic));
    }

    /**
     * Increment the value of the statistic.
     *
     * @param statistic the statistic
     */
    public void incrementValue(PlayerStatistic statistic, int value) {
        if (!this.values.containsKey(statistic)) {
            return;
        }

        this.values.put(statistic, String.valueOf(this.getIntValue(statistic) + value));
        PlayerStatisticsDao.updateStatistic(this.userId, statistic, this.values.get(statistic));
    }

    /**
     * Set the value of the statistic.
     *
     * @param statisticMap the statistic
     */
    public void setValues(int userId, HashMap<PlayerStatistic, String> statisticMap) {
        for (var statistic : statisticMap.entrySet()) {
            if (this.values.containsKey(statistic.getKey())) {
                this.values.put(statistic.getKey(), statistic.getValue());
            }
        }

        PlayerStatisticsDao.updateStatistics(userId, statisticMap);
    }

    /**
     * Get string value.
     *
     * @param value the statistic
     * @return the string
     */
    public String getValue(PlayerStatistic value) {
        return this.values.get(value);
    }
}
