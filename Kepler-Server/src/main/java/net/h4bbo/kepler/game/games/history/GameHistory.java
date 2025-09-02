package net.h4bbo.kepler.game.games.history;

import net.h4bbo.kepler.game.games.enums.GameType;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameHistory {
    private int id;
    private String name;
    private String gameCreator;
    private int mapId;
    private int winningTeam;
    private int winningTeamScore;
    private String extraData;
    private GameType gameType;

    private GameHistoryData gameHistoryData;

    public GameHistory(GameHistoryData gameHistoryData) {
        this.gameHistoryData = gameHistoryData;
    }

    public GameHistoryData getHistoryData() {
        return gameHistoryData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGameCreator() {
        return gameCreator;
    }

    public void setGameCreator(String gameCreator) {
        this.gameCreator = gameCreator;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public GameHistoryData getGameHistoryData() {
        return gameHistoryData;
    }

    public void setGameHistoryData(GameHistoryData gameHistoryData) {
        this.gameHistoryData = gameHistoryData;
    }

    public int getWinningTeam() {
        return winningTeam;
    }

    public void setWinningTeam(int winningTeam) {
        this.winningTeam = winningTeam;
    }

    public int getWinningTeamScore() {
        return winningTeamScore;
    }

    public void setWinningTeamScore(int winningTeamScore) {
        this.winningTeamScore = winningTeamScore;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public List<Integer> getAllowedPowerUps() {
        if (this.extraData.length() > 0) {
            return Stream.of(this.extraData.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    public Integer getGameLength() {
        if (this.gameType == GameType.SNOWSTORM) {
            var gameLengthChoice = Integer.parseInt(this.extraData);

            if (gameLengthChoice == 1) {
                return (int) TimeUnit.MINUTES.toSeconds(2);
            }

            if (gameLengthChoice == 2) {
                return (int) TimeUnit.MINUTES.toSeconds(3);
            }

            if (gameLengthChoice == 3) {
                return (int) TimeUnit.MINUTES.toSeconds(5);
            }
        }

        return 0;
    }
}
