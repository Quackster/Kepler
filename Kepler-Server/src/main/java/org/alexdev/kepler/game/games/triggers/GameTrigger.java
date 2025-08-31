package org.alexdev.kepler.game.games.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.games.gamehalls.GamehallGame;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.interactors.InteractionType;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.triggers.GenericTrigger;
import org.alexdev.kepler.messages.outgoing.rooms.games.CLOSEGAMEBOARD;
import org.alexdev.kepler.messages.outgoing.rooms.games.OPENGAMEBOARD;

import java.util.ArrayList;
import java.util.List;

public abstract class GameTrigger extends GenericTrigger {
    private List<GamehallGame> gameInstances;

    public GameTrigger() {
        this.gameInstances = new ArrayList<>();
    }

    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, boolean isRotation) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        // Call default sitting trigger
        InteractionType.CHAIR.getTrigger().onEntityStop(entity, roomEntity, item, isRotation);


        // Handle game logic from here
        GamehallGame instance = this.getGameInstance(item.getPosition());

        if (instance != null && !(instance.getRoomId() > 0)) {
            instance.setRoomId(roomEntity.getRoom().getId());
        }

        if (instance == null || (instance.getPlayers().size() >= instance.getMaximumPeopleRequired())) {
            return;
        }

        List<Player> joinedPlayers = instance.refreshPlayers();

        if (instance.getGameId() != null) {
            if (instance.getPlayers().size() >= instance.getMinimumPeopleRequired()) {

                for (Player p : joinedPlayers) {
                    p.send(new OPENGAMEBOARD(instance.getGameId(), instance.getGameFuseType())); // Player joined mid-game
                }
            }
        }

        if (instance.getGameId() == null) {
            if (instance.hasPlayersRequired()) { // New game started
                instance.createGameId();
                instance.gameStart();
                instance.sendToEveryone(new OPENGAMEBOARD(instance.getGameId(), instance.getGameFuseType()));;
            }
        }

        /*if (instance.getGameId() == null) {
            if (joinedPlayers.size() >= 1) { // New game started
                instance.createGameId();
                instance.gameStart();

                for (Player p : joinedPlayers) {
                    p.send(new OPENGAMEBOARD(instance.getGameId(), instance.getGameFuseType())); // Player joined mid-game
                }
            }
        } else {
            player.send(new OPENGAMEBOARD(instance.getGameId(), instance.getGameFuseType()));
        }*/
    }

    @Override
    public void onEntityLeave(Entity entity, RoomEntity roomEntity, Item item) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;
        GamehallGame instance = this.getGameInstance(item.getPosition());

        if (instance == null) {
            return;
        }

        if (player.getRoomUser().getCurrentGameId() == null) {
            return;
        }

        if (instance.getGameId() != null) { // If game has started
            int newPlayerCount = instance.getPlayers().size() - 1;

            if (newPlayerCount >= instance.getMinimumPeopleRequired()) {
                player.send(new CLOSEGAMEBOARD(instance.getGameId(), instance.getGameFuseType()));
            } else {
                instance.sendToEveryone(new CLOSEGAMEBOARD(instance.getGameId(), instance.getGameFuseType()));
            }
        }

        instance.getPlayers().remove(player);
        player.getRoomUser().setCurrentGameId(null);

        if (!instance.hasPlayersRequired()) {
            instance.resetGameId();
            instance.gameStop();
        }
    }

    /**
     * Gets the game instance on this specified position.
     *
     * @param position the position to look for the game instance
     * @return the game instance, if successful
     */
    public GamehallGame getGameInstance(Position position) {
        for (GamehallGame instances : this.gameInstances) {
            for (int[] coordinate : instances.getChairCoordinates()) {
                if (position.equals(new Position(coordinate[0], coordinate[1]))) {
                    return instances;
                }
            }
        }

        return null;
    }

    /**
     * Get all game instances.
     *
     * @return the list of game instances
     */
    public List<GamehallGame> getGameInstances() {
        return gameInstances;
    }

    /**
     * Gets the list of seats and their pairs as coordinates
     */
    public abstract List<List<int[]>> getChairGroups();
}
