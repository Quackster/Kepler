package org.alexdev.kepler.game.games.gamehalls;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.games.triggers.GameTrigger;
import org.alexdev.kepler.messages.outgoing.rooms.games.ITEMMSG;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameChess extends GamehallGame {
    private static class GameToken {
        private char token;

        private GameToken(char token) {
            this.token = token;
        }

        private char getToken() {
            return token;
        }
    }

    private boolean gameFinished;

    private Board board;
    private GameToken[] gameTokens;
    private Player nextTurn;

    private List<Player> playersInGame;
    private HashMap<Player, GameToken> playerSides;

    public GameChess(int roomId, List<int[]> chairs) {
        super(roomId, chairs);
    }

    @Override
    public void gameStart() {
        this.playersInGame =  new ArrayList<>();
        this.playerSides = new HashMap<>();
        this.restartMap();
    }

    @Override
    public void gameStop() {
        this.playersInGame.clear();
        this.playerSides.clear();
        this.board = null;
    }

    @Override
    public void handleCommand(Player player, Room room, Item item, String command, String[] args) {
        GameTrigger trigger = (GameTrigger) item.getItemTrigger();

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

            if (this.gameFinished) {
                player.send(new ITEMMSG(new String[]{this.getGameId(), "TYPERESERVED"})); // Alert/error sound!
                return;
            }

            player.send(new ITEMMSG(new String[]{this.getGameId(), "SELECTTYPE " + String.valueOf(sideChosen)}));

            GameToken token = this.getToken(sideChosen);

            this.playersInGame.add(player);
            this.playerSides.put(player, token);

            // Select the other side for the player
            GameToken otherToken = null;

            for (GameToken other : this.gameTokens) {
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
                        this.playerSides.put(otherPlayer, otherToken);
                        break;
                    }
                }
            }

            this.restartMap();
            this.broadcastMap();
        }

        if (command.equals("MOVEPIECE")) {
            if (this.nextTurn != player) {
                player.send(new ITEMMSG(new String[]{this.getGameId(), "TYPERESERVED"})); // Alert/error sound!
                this.broadcastMap();
                return;
            }

            if (this.gameFinished) {
                player.send(new ITEMMSG(new String[]{this.getGameId(), "TYPERESERVED"})); // Alert/error sound!
                this.broadcastMap();
                return;
            }

            if (this.playersInGame.size() < this.getMinimumPeopleRequired()) {
                this.broadcastMap();
                return; // Can't place objects until other player has joined.
            }

            if (!this.playerSides.containsKey(player)) {
                this.broadcastMap();
                return;
            }

            Square fromSquare = Square.valueOf(args[0].toUpperCase());
            Square toSquare = Square.valueOf(args[1].toUpperCase());

            if (fromSquare == toSquare) {
                return;
            }

            Move move = new Move(fromSquare, toSquare);
            boolean isLegalMove = false;

            try {
                var moveList = MoveGenerator.generateLegalMoves(this.board);
                isLegalMove = moveList.contains(move);

            } catch (MoveGeneratorException e) { }

            if (isLegalMove) {
                this.board.doMove(move, true);

                if (this.board.isDraw()) {
                    this.gameFinished = true;
                    this.showChat(null, "The chess game has ended in a draw");
                    return;
                } else if (this.board.isStaleMate()) {
                    this.gameFinished = true;
                    this.showChat(null, "The chess game has encountered a stalemate");
                    return;
                } else if (this.board.isMated()) {
                    this.gameFinished = true;
                    this.showChat(null, player.getDetails().getName() + " has won the chess game");
                    return;
                }

                this.swapTurns(player);
            }

            player.getRoomUser().getTimerManager().resetRoomTimer();
            this.broadcastMap();
        }

        if (command.equals("RESTART")) {
            this.restartMap();
            this.broadcastMap();
            return;
        }
    }

    /**
     * Send the game map to the opponents.
     */
    private void broadcastMap() {
        StringBuilder boardData = new StringBuilder();

        for (Square square : Square.values()) {
            Piece piece = this.board.getPiece(square);

            if (piece == null) {
                continue;
            }

            if (piece.getPieceType() == PieceType.NONE || piece.getPieceSide() == null) {
                continue;
            }

            String side = piece.getPieceSide() == Side.BLACK ? "B" : "W";
            String chessPiece = this.getChessPiece(piece.getPieceType());

            boardData.append(side);
            boardData.append(chessPiece);
            boardData.append(square.value().toLowerCase());
            boardData.append("\r");

        }

        String[] playerNames = this.getCurrentlyPlaying();
        this.sendToEveryone(new ITEMMSG(new String[]{this.getGameId(), "PIECEDATA", playerNames[0], playerNames[1], boardData.toString()}));
    }

    /**
     * Get the CCT type of chess piece by the piece type supplied.
     *
     * @param pieceType the piece type instance
     * @return the CCT type, else it defaults to Rook
     */
    public String getChessPiece(PieceType pieceType) {
        if (pieceType == PieceType.BISHOP) {
            return "cr";
        }

        if (pieceType == PieceType.KNIGHT) {
            return "hr";
        }

        if (pieceType == PieceType.KING) {
            return "kg";
        }

        if (pieceType == PieceType.QUEEN) {
            return "qu";
        }

        if (pieceType == PieceType.ROOK) {
            return "tw";
        }

        return "sd"; // Pawn
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
            playerNames[0] = Character.toUpperCase(this.playerSides.get(this.nextTurn).getToken()) + " " + this.nextTurn.getDetails().getName();
        }

        return playerNames;
    }

    private void showChat(Player player, String chat) {
        for (Player p : this.playersInGame) {
            if (player != null && p == player) {
                continue;
            }

            p.send(new CHAT_MESSAGE(CHAT_MESSAGE.ChatMessageType.CHAT, p.getRoomUser().getInstanceId(), chat));
        }
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
                new GameToken('w'),
                new GameToken('b')
        };

        if (this.playersInGame.size() > 0) {
            this.nextTurn = this.getPlayerBySide('w'); // White always goes first, according to chess rules.
        }

        this.gameFinished = false;
        this.board = new Board();
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

    /**
     * Locate a player instance by the side they're playing.
     *
     * @param side the side used
     * @return the player instance, if successful
     */
    private Player getPlayerBySide(char side) {
        for (var kvp : this.playerSides.entrySet()) {
            if (kvp.getValue().getToken() == side) {
                return kvp.getKey();
            }
        }

        return null;
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
        return "Chess";
    }
}
