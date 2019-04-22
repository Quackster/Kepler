package org.alexdev.kepler.game.games.gamehalls.utils;

public class GameToken {
    private char token;
    private char winningToken;
    private int moves;

    public GameToken(char token, char winningToken) {
        this.token = token;
        this.winningToken = winningToken;
        this.moves = 0;
    }

    public char getToken() {
        return token;
    }

    public char getWinningToken() {
        return winningToken;
    }

    public int getMoves() {
        return moves;
    }

    public void incrementMoves() {
        this.moves = this.moves + 1;
    }
}
