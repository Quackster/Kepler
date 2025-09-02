package net.h4bbo.kepler.game.games.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameHistoryData {
    private int teamCount;
    private List<GameHistoryPlayer> players;

    public GameHistoryData() {
        this.players = new ArrayList<>();
    }

    public void addPlayer(int userId, int points, int teamId) {
        this.players.add(new GameHistoryPlayer(points, teamId, userId));
    }

    public int getTeamCount() {
        return teamCount;
    }

    public void setTeamCount(int teamCount) {
        this.teamCount = teamCount;
    }

    public Map<Integer, List<GameHistoryPlayer>> getTeamData() {
        var teamHistory = new HashMap<Integer, List<GameHistoryPlayer>>();

        for (int i = 0; i < this.teamCount; i++) {
            var teamId = i;
            teamHistory.put(i, this.players.stream().filter(player -> player.getTeamId() == teamId).collect(Collectors.toList()));
        }

        return teamHistory;
    }
}
