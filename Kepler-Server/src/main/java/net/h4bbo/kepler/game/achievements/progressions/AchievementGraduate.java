package net.h4bbo.kepler.game.achievements.progressions;

import net.h4bbo.kepler.game.achievements.AchievementInfo;
import net.h4bbo.kepler.game.achievements.AchievementProgress;
import net.h4bbo.kepler.game.achievements.user.UserAchievement;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;
import net.h4bbo.kepler.util.config.GameConfiguration;

public class AchievementGraduate implements AchievementProgress {
    @Override
    public boolean tryProgress(Player player, UserAchievement userAchievement, AchievementInfo achievementInfo) {
        if (!GameConfiguration.getInstance().getBoolean("tutorial.enabled")) {
            return false;
        }

        //if (!player.getDetails().getTutorialFlags().contains(1)) {
            userAchievement.setProgress(achievementInfo.getProgressRequired());
            return true;
        //}

        //return false;
    }
}
