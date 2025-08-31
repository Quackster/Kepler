package org.alexdev.kepler.game.achievements;

import org.alexdev.kepler.game.achievements.user.UserAchievement;
import org.alexdev.kepler.game.player.Player;

public interface AchievementProgress {
    boolean tryProgress(Player player, UserAchievement userAchievement, AchievementInfo achievementInfo);
}
