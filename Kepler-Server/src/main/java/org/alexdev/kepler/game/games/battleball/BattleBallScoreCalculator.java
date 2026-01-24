package org.alexdev.kepler.game.games.battleball;

import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.games.player.score.ScoreCalculator;
import org.alexdev.kepler.game.games.utils.ScoreReference;

/**
 * Score calculator for BattleBall games.
 * Calculates scores based on tiles claimed by players/teams.
 */
public class BattleBallScoreCalculator implements ScoreCalculator {
    private final BattleBallGame game;

    public BattleBallScoreCalculator(BattleBallGame game) {
        this.game = game;
    }

    @Override
    public int calculatePlayerScore(GamePlayer player) {
        int score = 0;
        int userId = player.getUserId();

        for (BattleBallTile tile : game.getTiles()) {
            for (ScoreReference scoreReference : tile.getPointsReferece()) {
                if (scoreReference.getBy() == userId) {
                    score += scoreReference.getScore();
                }
            }
        }

        return score;
    }

    @Override
    public int calculateTeamScore(GameTeam team) {
        int score = 0;
        int teamId = team.getId();

        for (BattleBallTile tile : game.getTiles()) {
            for (ScoreReference scoreReference : tile.getPointsReferece()) {
                if (scoreReference.getGameTeam().getId() == teamId) {
                    score += scoreReference.getScore();
                }
            }
        }

        return score;
    }
}
