package org.alexdev.kepler.game.achievements;

import org.alexdev.kepler.dao.mysql.AchievementDao;
import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.game.achievements.user.UserAchievement;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.util.StringUtil;

import java.util.Map;

public class AchievementManager {
    private static AchievementManager instance;
    private Map<Integer, AchievementInfo> achievements;

    public AchievementManager() {
        this.achievements = AchievementDao.getAchievements();
    }

    /**
     * Method for handling achievement progression.
     *
     * @param achievementType the type of achievement
     * @param player the player to handle for
     */
    public void tryProgress(AchievementType achievementType, Player player) {
        UserAchievement userAchievement = player.getAchievementManager().locateAchievement(achievementType);

        if (userAchievement == null) {
            return;
        }

        if (achievementType.getProgressor().tryProgress(player, userAchievement, userAchievement.getAchievementInfo())) {
            AchievementDao.saveUserAchievement(player.getDetails().getId(), userAchievement);
        }

        if (userAchievement.getProgress() != userAchievement.getAchievementInfo().getProgressRequired()) {
            return;
        }

        var badgeCode = userAchievement.getAchievementInfo().getName() + userAchievement.getAchievementInfo().getLevel();

        if (userAchievement.getAchievementInfo().getName().equals("GL")) {
            badgeCode = userAchievement.getAchievementInfo().getName() + StringUtil.toAlphabetic(userAchievement.getAchievementInfo().getLevel());
        }

        if (player.getBadgeManager().hasBadge(badgeCode)) {
            return;
        }

        /*
        if (userAchievement.getAchievementInfo().getPixelReward() > 0) {
            CurrencyDao.increasePixels(player.getDetails(), userAchievement.getAchievementInfo().getPixelReward());
            player.send(new ActivityPointNotification(player.getDetails().getPixels(), ActivityPointNotification.ActivityPointAlertType.PIXELS_SOUND)); // Alert pixel sound
        }

         */

        AchievementInfo previousAchievement = locateAchievement(achievementType, userAchievement.getAchievementInfo().getLevel() - 1);

        if (previousAchievement != null) {
            var badgeRemoveCode = previousAchievement.getName() + previousAchievement.getLevel();

            /*
            if (badgeRemoveCode.equals("GL")) {
                badgeRemoveCode = userAchievement.getAchievementInfo().getName() + StringUtil.toAlphabetic(userAchievement.getAchievementInfo().getLevel() - 1);
            }
            */

            if (!achievementType.hasRemovePreviousAchievement()) {
                badgeRemoveCode = null;
            }

            player.getBadgeManager().tryAddBadge(badgeCode, badgeRemoveCode, 1);
        }else {
            player.getBadgeManager().tryAddBadge(badgeCode, null, userAchievement.getAchievementInfo().getLevel());
        }
    }

    /**
     * Locate achivemenet by the next level.
     *
     * @param achievementType the type of the achievement
     * @param nextLevel the next level
     * @return return the achievement found, if successful
     */
    public AchievementInfo locateAchievement(AchievementType achievementType, int nextLevel) {
        for (AchievementInfo achievementInfo : this.achievements.values()) {
            if (achievementInfo.getName().equals(achievementType.getName()) && achievementInfo.getLevel() == nextLevel) {
                return achievementInfo;
            }
        }

        return null;
    }

    /**
     * Locate the achievement by id.
     *
     * @param achievementId the achievement id
     * @return the achievement
     */
    public AchievementInfo getAchievement(int achievementId) {
        return this.achievements.get(achievementId);
    }

    /**
     * Get the list of achievements.
     *
     * @return the list of achievements
     */
    public Map<Integer, AchievementInfo> getAchievements() {
        return achievements;
    }

    /**
     * Get the {@link AchievementManager} instance
     *
     * @return the catalogue manager instance
     */
    public static AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }

        return instance;
    }

    /**
     * Reset the {@link AchievementManager} instance
     */
    public static void reset() {
        instance = null;
        getInstance();
    }
}
