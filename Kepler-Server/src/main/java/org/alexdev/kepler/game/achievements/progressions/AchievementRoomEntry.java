package org.alexdev.kepler.game.achievements.progressions;

import org.alexdev.kepler.dao.mysql.RoomVisitsDao;
import org.alexdev.kepler.game.achievements.AchievementInfo;
import org.alexdev.kepler.game.achievements.AchievementProgress;
import org.alexdev.kepler.game.achievements.user.UserAchievement;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.statistics.PlayerStatistic;

public class AchievementRoomEntry implements AchievementProgress {
    @Override
    public boolean tryProgress(Player player, UserAchievement userAchievement, AchievementInfo achievementInfo) {
        int progress = RoomVisitsDao.countVisits(player.getDetails().getId());

        if (progress > achievementInfo.getProgressRequired()) {
            progress = achievementInfo.getProgressRequired();
        }

        if (progress != userAchievement.getProgress()) {
            userAchievement.setProgress(progress);
            return true;
        }

        return false;
    }
}
