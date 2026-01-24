package org.alexdev.kepler.game.games.player;

import org.alexdev.kepler.game.games.Game;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameTeam {
    private final int id;
    private final Game game;
    private final List<GamePlayer> players = new CopyOnWriteArrayList<>();
    private final AtomicInteger points = new AtomicInteger(0);

    public GameTeam(int id, Game game) {
        this.id = id;
        this.game = game;
    }

    public int getId() {
        return id;
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public void calculateScore() {
        points.set(game.getScoreCalculator().calculateTeamScore(this));
    }

    public int getPoints() {
        return points.get();
    }

    public List<GamePlayer> getActivePlayers() {
        return players.stream().filter(GamePlayer::isInGame).collect(Collectors.toList());
    }
}
