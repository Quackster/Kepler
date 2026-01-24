package net.h4bbo.kepler.game.games.player.score;

import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.player.GameTeam;

/**
 * Strategy interface for calculating scores in different game types.
 */
public interface ScoreCalculator {
    /**
     * Calculate the score for an individual player.
     */
    int calculatePlayerScore(GamePlayer player);

    /**
     * Calculate the total score for a team.
     */
    int calculateTeamScore(GameTeam team);
}
