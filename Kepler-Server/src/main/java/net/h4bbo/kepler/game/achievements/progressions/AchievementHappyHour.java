package net.h4bbo.kepler.game.achievements.progressions;

import net.h4bbo.kepler.Kepler;
import net.h4bbo.kepler.game.achievements.AchievementInfo;
import net.h4bbo.kepler.game.achievements.AchievementProgress;
import net.h4bbo.kepler.game.achievements.user.UserAchievement;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;

public class AchievementHappyHour implements AchievementProgress {
    @Override
    public boolean tryProgress(Player player, UserAchievement userAchievement, AchievementInfo achievementInfo) {
        if (Kepler.isHappyHour()) {
            userAchievement.setProgress(achievementInfo.getProgressRequired());
            return true;
        }

        return false;
    }
}
