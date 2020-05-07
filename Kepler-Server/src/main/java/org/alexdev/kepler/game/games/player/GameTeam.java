package org.alexdev.kepler.game.games.player;

import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.battleball.BattleBallGame;
import org.alexdev.kepler.game.games.battleball.BattleBallTile;
import org.alexdev.kepler.game.games.utils.ScoreReference;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GameTeam {
    private int id;
    private Game game;
    private List<GamePlayer> playerList;
    private int score;

    public GameTeam(int id, Game game) {
        this.id = id;
        this.game = game;
        this.playerList = new CopyOnWriteArrayList<>();
        this.score = 0;
    }

    public int getId() {
        return id;
    }

    public List<GamePlayer> getPlayers() {
        return playerList;
    }

    public int getScore() {
        if (this.game instanceof BattleBallGame) {
            this.score = 0;

            BattleBallGame battleBallGame = (BattleBallGame) this.game;

            for (BattleBallTile battleBallTile : battleBallGame.getTiles()) {
                for (ScoreReference scoreReference : battleBallTile.getPointsReferece()) {
                    if (scoreReference.getGameTeam().getId() != this.id) {
                        continue;
                    }

                    this.score += scoreReference.getScore();
                }
            }
        }

        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<GamePlayer> getActivePlayers() {
        return playerList.stream().filter(GamePlayer::isInGame).collect(Collectors.toList());
    }
}
