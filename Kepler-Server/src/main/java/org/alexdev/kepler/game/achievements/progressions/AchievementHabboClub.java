package org.alexdev.kepler.game.achievements.progressions;

import org.alexdev.kepler.game.achievements.AchievementInfo;
import org.alexdev.kepler.game.achievements.AchievementProgress;
import org.alexdev.kepler.game.achievements.user.UserAchievement;
import org.alexdev.kepler.game.club.ClubSubscription;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.statistics.PlayerStatistic;

public class AchievementHabboClub implements AchievementProgress {
    @Override
    public boolean tryProgress(Player player, UserAchievement userAchievement, AchievementInfo achievementInfo) {
        boolean canProgress = false;

        if (achievementInfo.getLevel() == 1) {
            if (player.getDetails().hasClubSubscription()) {
                canProgress = true;
            }
        }

        if (achievementInfo.getLevel() == 2) {
            if (ClubSubscription.hasGoldClubSubscription(player)) {
                canProgress = true;
            }
        }

        if (achievementInfo.getLevel() == 3) {
            if (ClubSubscription.hasPlatinumClubSubscription(player)) {
                canProgress = true;
            }
        }

        if (canProgress) {
            userAchievement.setProgress(achievementInfo.getProgressRequired());
            return true;
        }

        return false;
    }
}
