package net.h4bbo.kepler.game.achievements.progressions;

import net.h4bbo.kepler.dao.mysql.RoomVisitsDao;
import net.h4bbo.kepler.game.achievements.AchievementInfo;
import net.h4bbo.kepler.game.achievements.AchievementProgress;
import net.h4bbo.kepler.game.achievements.user.UserAchievement;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;

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
