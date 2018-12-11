package org.alexdev.kepler.game.games.gamehalls;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.game.games.triggers.GameTrigger;
import org.alexdev.kepler.messages.types.MessageComposer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public abstract class GamehallGame {
    private String gameId;
    private int roomId;

    private List<int[]> chairCoordinates;
    private List<Player> players;

    public GamehallGame(int roomId, List<int[]> chairCoordinateList) {
        this.roomId = roomId;
        this.chairCoordinates = chairCoordinateList;
        this.players = new CopyOnWriteArrayList<>();
    }

    /**
     * Handler for when the game starts.
     */
    public abstract void gameStart();

    /**
     * Handler for when the game stops.
     */
    public abstract void gameStop();

    /**
     * Handle the incoming packet data from the game commands.
     *
     * @param player the player doing the command
     * @param room the room the game is in
     * @param item the item that the player is sitting on
     * @param args the arguments
     */
    public abstract void handleCommand(Player player, Room room, Item item, String command, String[] args);

    /**
     * Gets the unique game ID instance for this pair. Will
     * return null if game has not initialised.
     *
     * @return the game id
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Generate the unique game ID instance for this pair.
     */
    public void createGameId() {
        String alphabet = "abcdefghijlmnopqrstuvwyz1234567890";
        StringBuilder gameId = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            gameId.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(0, alphabet.length())));
        }

        this.gameId = gameId.toString();

        for (Player player : this.players) {
            player.getRoomUser().setCurrentGameId(this.gameId);
        }
    }

    /**
     * Resets the game ID back to null.
     */
    public void resetGameId() {
        this.gameId = null;
    }

    /**
     * Get the room instance this game instance is running in.
     *
     * @return the room instance
     */
    public Room getRoom() {
        return RoomManager.getInstance().getRoomById(this.roomId);
    }

    /**
     * Get the opponents (not including the user supplied).
     *
     * @param player the player to exclude
     * @return the list of opponents
     */
    public List<Player> getOpponents(Player player) {
        return this.players.stream().filter(p -> p.getDetails().getId() != player.getDetails().getId()).collect(Collectors.toList());
    }

    /**
     * Send a packet to all opponents except the user supplied
     *
     * @param player the player to exclude
     * @param messageComposer the message to send
     */
    public void sendToOpponents(Player player, MessageComposer messageComposer) {
        for (Player p : this.getOpponents(player)) {
            p.send(messageComposer);
        }
    }

    /**
     * Send a packet to everyone playing
     *
     * @param messageComposer the packet to send
     */
    public void sendToEveryone(MessageComposer messageComposer) {
        for (Player p : this.players) {
            p.send(messageComposer);
        }
    }

    /**
     * Get the list of players at each table, this list dictates who is actually at a table. Create your own list
     * in each {@link GamehallGame} implementation for currently active players, for example look at {@link GameTicTacToe}
     * @return the list of players at each table
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Get if the server has the correct amount of players required before the game starts.
     *
     * @return true, if successful
     */
    public boolean hasPlayersRequired() {
        return this.players.size() >= this.getMinimumPeopleRequired();
    }

    /**
     * Refresh players currently playing.
     *
     * @return the list of new players found
     */
    public List<Player> refreshPlayers() {
        this.players.forEach(p -> {
            if (p.getRoomUser().getRoom() == null || !this.hasPosition(p.getRoomUser().getPosition())) {
                this.players.remove(p);
            }
        });

        List<Player> newPlayers = new ArrayList<>();

        for (RoomTile roomTile : this.getTiles()) {
            if (roomTile.getEntities().isEmpty()) {
                continue;
            }

            Entity entity = roomTile.getEntities().get(0);
            Player player = (Player) entity;

            if (entity.getType() != EntityType.PLAYER) {
                continue;
            }

            if (player.getRoomUser().getCurrentGameId() != null) {
                continue;
            }

            if (this.players.contains(player)) {
                continue;
            }

            player.getRoomUser().setCurrentGameId(this.gameId);

            this.players.add(player);
            newPlayers.add(player);
        }

        return newPlayers;
    }

    /**
     * Return the room tiles for this room.
     *
     * @return the list of room tiles
     */
    public List<RoomTile> getTiles() {
        List<RoomTile> tiles = new ArrayList<>();
        Room room = this.getRoom();

        if (room == null) {
            return tiles;
        }

        for (var coord : this.chairCoordinates) {
            RoomTile roomTile = room.getMapping().getTile(coord[0], coord[1]);

            if (roomTile == null) {
                continue;
            }

            tiles.add(roomTile);
        }

        return tiles;
    }

    /**
     * If this position is invalid, as in, the position is a chair to play on
     * @param position the position
     * @return true, if successful
     */
    private boolean hasPosition(Position position) {
        for (RoomTile roomTile : this.getTiles()) {
            if (roomTile.getPosition().equals(position)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get FUSE game type
     *
     * @return the game type
     */
    public abstract String getGameFuseType();

    /**
     * Get the minimum people required for a game to start
     *
     * @return the required amount of people
     */
    public abstract int getMinimumPeopleRequired();

    /**
     * Get the maximum people required before no one else is allowed to join
     *
     * @return the max people required
     */
    public abstract int getMaximumPeopleRequired();

    /**
     * Restarts the game.
     *
     * @param trigger the trigger used for this game piece
     * @param item the item that the user is sitting on
     */
    public void restartGame(GameTrigger trigger, Item item){
        for (Player player : this.players) {
            trigger.onEntityLeave(player, player.getRoomUser(), item);
        }
    }
}
