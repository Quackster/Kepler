package org.alexdev.kepler.game.games.utils;

import org.alexdev.kepler.game.games.player.GameTeam;

public class ScoreReference {
    private int score;
    private GameTeam gameTeam;
    private int by;

    public ScoreReference(int score, GameTeam gameTeam, int by) {
        this.score = score;
        this.gameTeam = gameTeam;
        this.by = by;
    }

    public int getScore() {
        return score;
    }

    public GameTeam getGameTeam() {
        return gameTeam;
    }

    public int getBy() {
        return by;
    }
}
