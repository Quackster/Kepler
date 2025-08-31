package org.alexdev.kepler.game.achievements.user;

import org.alexdev.kepler.game.achievements.AchievementInfo;
import org.alexdev.kepler.game.achievements.AchievementManager;

public class UserAchievement {
    private int achievementId;
    private int userId;
    private int progress;

    public UserAchievement(int achievementId, int userId, int progress) {
        this.achievementId = achievementId;
        this.userId = userId;
        this.progress = progress;
    }

    /**
     * Get the achievement information.
     *
     * @return the achievement information
     */
    public AchievementInfo getAchievementInfo() {
        return AchievementManager.getInstance().getAchievement(this.achievementId);
    }

    /**
     * Get the user id who has this achievement.
     *
     * @return the user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Get the current progress of the achievement.
     *
     * @return the progress
     */
    public int getProgress() {
        return progress;
    }

    /**
     * Set the current progress of the achievement.
     *
     * @param progress the current progress
     */
    public void setProgress(int progress) {
        this.progress = progress;
    }
}
