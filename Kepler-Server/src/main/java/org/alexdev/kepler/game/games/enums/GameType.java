package org.alexdev.kepler.game.games.enums;

public enum GameType {
    BATTLEBALL("bb_lobby_1"),
    SNOWSTORM("snowwar_lobby_1");

    private String lobbyModel;

    GameType(String lobbyModel) {
        this.lobbyModel = lobbyModel;
    }

    public String getLobbyModel() {
        return lobbyModel;
    }
}
