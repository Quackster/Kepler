package net.h4bbo.kepler.game.games.history;

public class ScoreEntry {
    private String playerName;
    private long score;
    private int position;

    public ScoreEntry(String playerName, long score, int position) {
        this.playerName = playerName;
        this.score = score;
        this.position = position;
    }

    public String getPlayerName() {
        return playerName;
    }

    public long getScore() {
        return score;
    }

    public int getPosition() {
        return position;
    }
}
