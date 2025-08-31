package org.alexdev.kepler.game.achievements.progressions;

import org.alexdev.kepler.game.achievements.AchievementInfo;
import org.alexdev.kepler.game.achievements.AchievementProgress;
import org.alexdev.kepler.game.achievements.user.UserAchievement;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.statistics.PlayerStatistic;
import org.alexdev.kepler.util.config.GameConfiguration;

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
