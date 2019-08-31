package org.alexdev.kepler.game.games;

import org.alexdev.kepler.dao.mysql.GameDao;
import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GameFinishTask implements Runnable {
    private final List<GamePlayer> players;
    private final Map<Integer, GameTeam> teams;
    private final GameType gameType;

    public GameFinishTask(GameType gameType, List<GamePlayer> players, Map<Integer, GameTeam> teams) {
        this.gameType = gameType;
        this.players = players;
        this.teams = teams;
    }


    @Override
    public void run() {
        var sortedTeamList = new ArrayList<>(this.teams.values());
        sortedTeamList.sort(Comparator.comparingInt(GameTeam::getScore).reversed());

        for (GamePlayer gamePlayer : this.players) {
            GameDao.increasePoints(gamePlayer.getPlayer().getDetails(), this.gameType, gamePlayer.getScore());
        }
    }
}
