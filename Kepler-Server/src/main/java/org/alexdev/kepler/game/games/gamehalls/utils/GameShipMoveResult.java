package org.alexdev.kepler.game.games.gamehalls.utils;

public enum  GameShipMoveResult {
    HIT("X"),
    MISS("O"),
    SINK("S");

    private final String symbol;

    GameShipMoveResult(String symbol) {
    this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
