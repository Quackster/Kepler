package net.h4bbo.kepler.game.games.player;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.battleball.BattleBallGame;
import net.h4bbo.kepler.game.games.battleball.BattleBallTile;
import net.h4bbo.kepler.game.games.utils.ScoreReference;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameTeam {
    private int id;
    private Game game;
    private List<GamePlayer> playerList;

    public AtomicInteger points;
    public AtomicInteger snowstormPoints;

    public GameTeam(int id, Game game) {
        this.id = id;
        this.game = game;
        this.playerList = new CopyOnWriteArrayList<>();
        this.points = new AtomicInteger(0);
    }

    public int getId() {
        return id;
    }

    public List<GamePlayer> getPlayers() {
        return playerList;
    }

    public void calculateScore() {
        this.points.set(0);

        if (this.game instanceof BattleBallGame) {
            BattleBallGame battleBallGame = (BattleBallGame) this.game;

            for (BattleBallTile battleBallTile : battleBallGame.getTiles()) {
                for (ScoreReference scoreReference : battleBallTile.getPointsReferece()) {
                    if (scoreReference.getGameTeam().getId() != this.id) {
                        continue;
                    }

                    this.points.addAndGet(scoreReference.getScore());
                }
            }
        }
        else {
            this.points.set(0);

            int totalScore = this.playerList.stream()
                    .mapToInt(player -> player.getScore())
                    .sum();

            this.points.addAndGet(totalScore);
        }
    }

    public int getPoints() {
        return points.get();
    }



    public List<GamePlayer> getActivePlayers() {
        return playerList.stream().filter(GamePlayer::isInGame).collect(Collectors.toList());
    }
}
