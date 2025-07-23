package org.alexdev.kepler.game.games.gamehalls;

import org.alexdev.kepler.game.games.gamehalls.utils.GameToken;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.games.triggers.GameTrigger;
import org.alexdev.kepler.messages.outgoing.rooms.games.ITEMMSG;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE.ChatMessageType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameTicTacToe extends GamehallGame {
    private static final int NUM_IN_ROW = 5;
    private static final int MAX_WIDTH = 23;
    private static final int MAX_LENGTH = 24;

    private GameToken[] gameTokens;

    private List<Player> playersInGame;
    private Map<Player, Character> playerSides;

    private boolean gameFinished;
    private char[][] gameMap;
    private Player nextTurn;

    public GameTicTacToe(List<int[]> chairs) {
        super(chairs);
    }

    @Override
    public void gameStart() {
        this.playersInGame = new ArrayList<>();
        this.playerSides = new HashMap<>();
        this.restartMap();
    }

    @Override
    public void gameStop() {
        this.playersInGame.clear();
        this.playerSides.clear();
        this.gameMap = null;
    }

    @Override
    public void handleCommand(Player player, Room room, Item item, String command, String[] args) {
        GameTrigger trigger = (GameTrigger) item.getDefinition().getInteractionType().getTrigger();

        if (command.equals("CLOSE")) {
            trigger.onEntityLeave(player, player.getRoomUser(), item);
            return;
        }

        if (command.equals("CHOOSETYPE")) {
            char sideChosen = args[0].charAt(0);

            if (this.getToken(sideChosen) == null) {
                return;
            }

            if (this.getPlayerBySide(sideChosen) != null) {
                player.send(new ITEMMSG(new String[]{this.getGameId(), "TYPERESERVED"}));
                return;
            }

            this.playerSides.put(player, sideChosen);
            this.playersInGame.add(player);

            player.send(new ITEMMSG(new String[]{this.getGameId(), "SELECTTYPE " + String.valueOf(sideChosen)}));

            // Select the other side for the player
            GameToken otherToken = null;

            for (GameToken other : gameTokens) {
                if (other.getToken() != sideChosen) {
                    otherToken = other;
                    break;
                }
            }

            if (otherToken != null) {
                for (Player otherPlayer : this.getPlayers()) {
                    if (otherPlayer != player) {
                        otherPlayer.send(new ITEMMSG(new String[]{this.getGameId(), "SELECTTYPE " + String.valueOf(otherToken.getToken())}));
                        this.playersInGame.add(otherPlayer);
                        this.playerSides.put(otherPlayer, otherToken.getToken());
                        break;
                    }
                }
            }

            String[] playerNames = this.getCurrentlyPlaying();
            this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "OPPONENTS", playerNames[0], playerNames[1]}));
        }

        if (command.equals("RESTART")) {
            this.restartMap();
            this.broadcastMap();
            return;
        }

        if (command.equals("SETSECTOR")) {
            if (this.playersInGame.size() < this.getMinimumPeopleRequired()) {
                return; // Can't place objects until other player has joined.
            }

            if (!this.playerSides.containsKey(player)) {
                return;
            }

            if (this.nextTurn != player) {
                player.send(new ITEMMSG(new String[]{this.getGameId(), "TYPERESERVED"})); // Alert/error sound!
                return;
            }

            if (this.gameFinished) {
                player.send(new ITEMMSG(new String[]{this.getGameId(), "TYPERESERVED"})); // Alert/error sound!
                return;
            }

            char side = args[0].charAt(0);

            if (this.playerSides.get(player) != side) {
                return;
            }

            int Y = Integer.parseInt(args[1]);
            int X = Integer.parseInt(args[2]);

            if (X >= MAX_WIDTH || Y >= MAX_LENGTH) {
                return;
            }

            if (this.gameMap == null) {
                return;
            }

            if (this.gameMap[X][Y] != '0') {
                player.send(new ITEMMSG(new String[]{this.getGameId(), "TYPERESERVED"})); // Alert/error sound!
                return;
            }

            GameToken token = this.getToken(this.playerSides.get(player));
            token.incrementMoves();

            this.gameMap[X][Y] = token.getToken();

            Pair<Character, List<int[]>> variables = this.hasGameFinished();

            if (variables != null) {
                this.gameFinished = true;
                this.announceWinningSide(variables);
            } else {
                this.swapTurns(player);
            }

            player.getRoomUser().getTimerManager().resetRoomTimer();
            this.broadcastMap();
        }
    }

    /**
     * Announce the winning side, change the characters to their winning symbols, and says
     * how many moves it took.
     *
     * @param variables the winning character and coordinates of winning tiles
     */
    private void announceWinningSide(Pair<Character, List<int[]>> variables) {
        GameToken token = null;

        for (GameToken side : gameTokens) {
            if (side.getToken() == variables.getKey()) {
                token = side;
            }
        }

        if (token != null) {
            for (int[] coord : variables.getValue()) {
                this.gameMap[coord[0]][coord[1]] = token.getWinningToken();
            }

            this.broadcastMap();

            Player winner = this.getPlayerBySide(token.getToken());

            if (winner == null) {
                return;
            }

            for (Player player : this.playersInGame) {
                player.send(new CHAT_MESSAGE(ChatMessageType.CHAT, player.getRoomUser().getInstanceId(), winner.getDetails().getName() + " has won the game in " + token.getMoves() + " moves"));
            }
        }
    }

    /**
     * Check for the winner.
     *
     * @return a pair containing the winning character and coordinates of winning tiles, or null if no winner
     */
    private Pair<Character, List<int[]>> hasGameFinished() {
        // Direction vectors: horizontal, vertical, diagonal (\), diagonal (/)
        int[][] directions = {
            {0, 1},   // horizontal
            {1, 0},   // vertical  
            {1, 1},   // diagonal \
            {-1, 1}   // diagonal /
        };
        
        for (int row = 0; row < MAX_WIDTH; row++) {
            for (int col = 0; col < MAX_LENGTH; col++) {
                char startChar = gameMap[row][col];
                
                if (startChar == '0') {
                    continue;
                }
                
                // Check each direction from this starting position
                for (int[] direction : directions) {
                    List<int[]> winningCoords = checkDirection(row, col, direction[0], direction[1], startChar);
                    
                    if (winningCoords != null && winningCoords.size() >= NUM_IN_ROW) {
                        return Pair.of(startChar, winningCoords);
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Check for consecutive matching characters in a specific direction.
     *
     * @param startRow starting row position
     * @param startCol starting column position  
     * @param deltaRow row direction increment
     * @param deltaCol column direction increment
     * @param targetChar character to match
     * @return list of winning coordinates if found, null otherwise
     */
    private List<int[]> checkDirection(int startRow, int startCol, int deltaRow, int deltaCol, char targetChar) {
        List<int[]> coordinates = new ArrayList<>();
        
        for (int step = 0; step < NUM_IN_ROW; step++) {
            int currentRow = startRow + (step * deltaRow);
            int currentCol = startCol + (step * deltaCol);
            
            if (!isValidPosition(currentRow, currentCol)) {
                return null;
            }
            
            char currentChar = gameMap[currentRow][currentCol];
            
            if (currentChar != targetChar) {
                return null;
            }
            
            coordinates.add(new int[]{currentRow, currentCol});
        }
        
        return coordinates;
    }
    
    /**
     * Check if the given position is within board bounds.
     *
     * @param row row position
     * @param col column position
     * @return true if position is valid, false otherwise
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < MAX_WIDTH && col >= 0 && col < MAX_LENGTH;
    }

    /**
     * Swap who's turn it is to play.
     *
     * @param player the player to swap away from
     */
    private void swapTurns(Player player) {
        Player nextPlayer = null;

        if (this.nextTurn == player) {
            for (Player p :  this.playersInGame) {
                if (p != player) {
                    nextPlayer = p;
                }
            }
        }

        this.nextTurn = nextPlayer;
    }

    /**
     * Reset the game map.
     */
    private void restartMap() {
        this.gameTokens = new GameToken[]{
                new GameToken('O', 'q'),
                new GameToken('X', '+')
        };

        if (this.playersInGame.size() > 0) {
            this.nextTurn = this.playersInGame.get(0);
        }

        this.gameFinished = false;
        this.gameMap = new char[MAX_WIDTH][MAX_LENGTH];

        for (int X = 0; X < MAX_WIDTH; X++) {
            for (int Y = 0; Y < MAX_LENGTH; Y++) {
                this.gameMap[X][Y] = '0';
            }
        }
    }

    /**
     * Send the game map to the opponents.
     */
    private void broadcastMap() {
        StringBuilder boardData = new StringBuilder();

        for (char[] mapData : this.gameMap) {
            for (char mapLetter : mapData) {
                boardData.append(mapLetter == '0' ? (char)32 : mapLetter);
            }

            boardData.append((char)32);
        }

        String[] playerNames = this.getCurrentlyPlaying();
        this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "BOARDDATA", playerNames[0], playerNames[1], boardData.toString()}));
    }

    /**
     * Get the name of the user(s) currently playing as an array for the packet
     *
     * @return the array with player name
     */
    private String[] getCurrentlyPlaying() {
        String[] playerNames = new String[]{"", ""};

        /*for (int i = 0; i < this.playersInGame.size(); i++) {
            Player player = this.playersInGame.get(i);
            playerNames[i] = Character.toUpperCase(this.playerSides.get(player).getToken()) + " " + player.getDetails().getName();
        }*/

        if (this.nextTurn != null) {
            playerNames[0] = Character.toUpperCase(this.playerSides.get(this.nextTurn)) + " " + this.nextTurn.getDetails().getName();
        }

        return playerNames;
    }

    /**
     * Locate a player instance by the side they're playing.
     *
     * @param side the side used
     * @return the player instance, if successful
     */
    private Player getPlayerBySide(char side) {
        for (var kvp : this.playerSides.entrySet()) {
            if (kvp.getValue() == side) {
                return kvp.getKey();
            }
        }

        return null;
    }

    /**
     * Get token instance by character.
     *
     * @param side the character to compare against
     * @return the instance, if successful
     */
    private GameToken getToken(char side) {
        GameToken token = null;

        for (GameToken t : gameTokens) {
            if (t.getToken() == side) {
                token = t;
                break;
            }
        }

        return token;
    }

    @Override
    public int getMaximumPeopleRequired() {
        return 2;
    }

    @Override
    public int getMinimumPeopleRequired() {
        return 2;
    }

    @Override
    public String getGameFuseType() {
        return "TicTacToe";
    }
}
