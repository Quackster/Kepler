package net.h4bbo.kepler.game.achievements.progressions;

import net.h4bbo.kepler.dao.mysql.TagDao;
import net.h4bbo.kepler.game.achievements.AchievementInfo;
import net.h4bbo.kepler.game.achievements.AchievementProgress;
import net.h4bbo.kepler.game.achievements.user.UserAchievement;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;

public class AchievementTags implements AchievementProgress {
    @Override
    public boolean tryProgress(Player player, UserAchievement userAchievement, AchievementInfo achievementInfo) {
        var tagList = TagDao.getUserTags(player.getDetails().getId());

        int progress = tagList.size();

        if (progress >= 5) {
            progress = achievementInfo.getProgressRequired();
        }

        if (progress >= achievementInfo.getProgressRequired()) {
            userAchievement.setProgress(progress);
            return true;
        }


        return false;
    }
}