package net.h4bbo.kepler.game.achievements;

import net.h4bbo.kepler.game.achievements.user.UserAchievement;
import net.h4bbo.kepler.game.player.Player;

public interface AchievementProgress {
    boolean tryProgress(Player player, UserAchievement userAchievement, AchievementInfo achievementInfo);
}
