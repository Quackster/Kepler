package org.alexdev.kepler.game.games.player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GameTeam {
    private int id;
    private int loopScore;
    private List<GamePlayer> playerList;

    public GameTeam(int id) {
        this.id = id;
        this.playerList = new CopyOnWriteArrayList<>();
    }

    public int getId() {
        return id;
    }

    public List<GamePlayer> getActivePlayers() {
        return playerList.stream().filter(GamePlayer::isInGame).collect(Collectors.toList());
    }

    public List<GamePlayer> getPlayers() {
        return playerList;
    }

    public int getScore() {
        int score = 0;

        for (GamePlayer gamePlayer : this.getActivePlayers()) {
            score += gamePlayer.getScore();
        }

        return score;
    }

    public void setSealedTileScore() {
        for (GamePlayer p : this.getActivePlayers()) {
            p.setScore(p.getScore() + 14); // 14 because wiki said so
        }
    }
}
