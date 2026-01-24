package org.alexdev.kepler.game.games.snowstorm;

import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.games.player.score.ScoreCalculator;

/**
 * Score calculator for SnowStorm games.
 * Player scores are set directly during gameplay (hits/stuns).
 * Team scores are the sum of all player scores.
 */
public class SnowStormScoreCalculator implements ScoreCalculator {

    @Override
    public int calculatePlayerScore(GamePlayer player) {
        // SnowStorm scores are set directly via setScore() during gameplay
        // This method returns the current score without recalculating
        return player.getScore();
    }

    @Override
    public int calculateTeamScore(GameTeam team) {
        return team.getPlayers().stream()
                .mapToInt(GamePlayer::getScore)
                .sum();
    }
}
