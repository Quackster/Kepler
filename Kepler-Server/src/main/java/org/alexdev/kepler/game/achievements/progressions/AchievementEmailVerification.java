package org.alexdev.kepler.game.achievements.progressions;

import org.alexdev.kepler.game.achievements.AchievementInfo;
import org.alexdev.kepler.game.achievements.AchievementProgress;
import org.alexdev.kepler.game.achievements.user.UserAchievement;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.statistics.PlayerStatistic;

public class AchievementEmailVerification implements AchievementProgress {
    @Override
    public boolean tryProgress(Player player, UserAchievement userAchievement, AchievementInfo achievementInfo) {
        return false;
    }
}
