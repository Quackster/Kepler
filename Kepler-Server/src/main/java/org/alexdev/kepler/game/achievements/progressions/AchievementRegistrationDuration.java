package org.alexdev.kepler.game.achievements.progressions;

import org.alexdev.kepler.game.achievements.AchievementInfo;
import org.alexdev.kepler.game.achievements.AchievementProgress;
import org.alexdev.kepler.game.achievements.user.UserAchievement;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.statistics.PlayerStatistic;
import org.alexdev.kepler.util.DateUtil;

import java.util.concurrent.TimeUnit;

public class AchievementRegistrationDuration implements AchievementProgress {
    @Override
    public boolean tryProgress(Player player, UserAchievement userAchievement, AchievementInfo achievementInfo) {
        int daysSinceJoined = (int) Math.floor(TimeUnit.SECONDS.toDays((long) (DateUtil.getCurrentTimeSeconds() - Math.floor(player.getDetails().getJoinDate()))));

        if (daysSinceJoined >= achievementInfo.getProgressRequired()) {
            userAchievement.setProgress(achievementInfo.getProgressRequired());
            return true;
        }

        return false;
    }
}
