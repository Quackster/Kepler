package net.h4bbo.kepler.game.achievements.progressions;

import net.h4bbo.kepler.game.achievements.AchievementInfo;
import net.h4bbo.kepler.game.achievements.AchievementProgress;
import net.h4bbo.kepler.game.achievements.user.UserAchievement;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.statistics.PlayerStatistic;

public class AchievementLogin implements AchievementProgress {
    @Override
    public boolean tryProgress(Player player, UserAchievement userAchievement, AchievementInfo achievementInfo) {
        int progress = 0;

        /*
        if (!player.getDetails().getPreviousRespectDay().equals(DateUtil.getCurrentDate(DateUtil.SHORT_DATE))) {
            String yesterday = DateUtil.getDate(DateUtil.getCurrentTimeSeconds() - TimeUnit.DAYS.toSeconds(1), DateUtil.SHORT_DATE);

            if (yesterday.equals(player.getDetails().getPreviousRespectDay())) {
                progress++;
            } else {
                player.getStatisticManager().setLongValue(PlayerStatistic.DAYS_LOGGED_IN_ROW, 0);
            }
        }
*/
        /*PlayerStatisticsDao.getStatistic(player.getDetails().getId(), PlayerStatistic.DAYS_LOGGED_IN_ROW);
        if (TimeUnit.SECONDS.toDays(daysBtwLastLogin) > 1) {
            progress = 0;
        }
        else if (TimeUnit.SECONDS.toDays(daysBtwLastLogin) == 1) {
            progress++;
        }*/

        if (progress > 0) {
            player.getStatisticManager().incrementValue(PlayerStatistic.DAYS_LOGGED_IN_ROW, progress);
            progress = player.getStatisticManager().getIntValue(PlayerStatistic.DAYS_LOGGED_IN_ROW);
        }

        if (progress > achievementInfo.getProgressRequired()) {
            progress = achievementInfo.getProgressRequired();
        }

        if (progress > 0) {
            userAchievement.setProgress(progress);
            return true;
        }

        return false;
    }
}