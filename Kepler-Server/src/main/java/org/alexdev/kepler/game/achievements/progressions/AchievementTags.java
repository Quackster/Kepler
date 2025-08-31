package org.alexdev.kepler.game.achievements.progressions;

import org.alexdev.kepler.dao.mysql.TagDao;
import org.alexdev.kepler.game.achievements.AchievementInfo;
import org.alexdev.kepler.game.achievements.AchievementProgress;
import org.alexdev.kepler.game.achievements.user.UserAchievement;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.statistics.PlayerStatistic;

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