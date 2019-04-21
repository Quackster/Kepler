package org.alexdev.kepler.game.games.snowstorm;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.games.enums.GameState;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.games.snowstorm.events.SnowStormAvatarMoveEvent;
import org.alexdev.kepler.game.games.snowstorm.events.SnowStormObjectEvent;
import org.alexdev.kepler.game.games.snowstorm.object.SnowStormAvatarObject;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pathfinder.Rotation;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.games.SNOWSTORM_GAMESTATUS;

import java.util.ArrayList;
import java.util.List;

public class SnowstormUpdateTask implements Runnable {
    private final Room room;
    private final SnowStormGame game;

    public SnowstormUpdateTask(Room room, SnowStormGame game) {
        this.room = room;
        this.game = game;
    }

    @Override
    public void run() {
        try {
            if (!this.game.isGameStarted() || this.game.getGameState() == GameState.ENDED || this.game.getPlayers().isEmpty()) {
                return; // Don't send any packets or do any logic checks during when the game is finished
            }

            this.game.getExecutingEvents().clear();
            List<GamePlayer> playersToUpdate = new ArrayList<>();

            for (GameTeam gameTeam : this.game.getTeams().values()) {
                for (GamePlayer gamePlayer : gameTeam.getPlayers()) {
                    Player player = gamePlayer.getPlayer();

                    if (player != null
                            && player.getRoomUser().getRoom() != null
                            && player.getRoomUser().getRoom() == this.room) {

                        this.processEntity(gamePlayer, this.game);
                        RoomEntity roomEntity = player.getRoomUser();

                        if (roomEntity.isNeedsUpdate()) {
                            roomEntity.setNeedsUpdate(false);
                        }

                        // objects.add(new SnowStormAvatarObject(gamePlayer));
                        //objects.add(new SnowStormPlayerObject(gamePlayer));
                        //.add(new SnowStormObjectEvent(new SnowStormAvatarObject(gamePlayer)));

                        playersToUpdate.add(gamePlayer);
                    }
                }
            }

            if (game.getExecutingEvents().isEmpty()) {
                //return;
            }

            this.game.getExecutingEvents().removeIf(event -> event.getGameObjectType() == null);

            for (GamePlayer gamePlayer : playersToUpdate) {
                gamePlayer.getTurnContainer().iterateTurn();
                gamePlayer.getTurnContainer().calculateChecksum(game.getObjects());
                gamePlayer.getPlayer().send(new SNOWSTORM_GAMESTATUS(gamePlayer, this.game.getExecutingEvents()));
            }

        } catch (Exception ex) {
            Log.getErrorLogger().error("SnowstormUpdateTask crashed: ", ex);
        }
    }

    /**
     * Process entity.
     */
    private void processEntity(GamePlayer gamePlayer, SnowStormGame game) {
        Entity entity = (Entity) gamePlayer.getPlayer();
        RoomEntity roomEntity = entity.getRoomUser();

        SnowStormAvatarObject snowStormObject = (SnowStormAvatarObject)gamePlayer.getGameObject();

        Position position = roomEntity.getPosition();
        Position goal = roomEntity.getGoal();

        if (roomEntity.isWalking()) {
            // Apply next tile from the tile we removed from the list the cycle before
            if (roomEntity.getNextPosition() != null) {
                roomEntity.getPosition().setX(roomEntity.getNextPosition().getX());
                roomEntity.getPosition().setY(roomEntity.getNextPosition().getY());
                roomEntity.updateNewHeight(roomEntity.getPosition());

            }

            // We still have more tiles left, so lets continue moving
            if (roomEntity.getPath().size() > 0) {
                Position next = roomEntity.getPath().pop();

                // Tile was invalid after we started walking, so lets try again!
                if (!RoomTile.isValidTile(this.room, entity, next)) {
                    entity.getRoomUser().getPath().clear();
                    roomEntity.walkTo(goal.getX(), goal.getY());
                    this.processEntity(gamePlayer, game);
                    return;
                }

                RoomTile previousTile = roomEntity.getTile();
                previousTile.removeEntity(entity);

                RoomTile nextTile = roomEntity.getRoom().getMapping().getTile(next);
                nextTile.addEntity(entity);

                int rotation = Rotation.calculateWalkDirection(position.getX(), position.getY(), next.getX(), next.getY());
                roomEntity.getPosition().setRotation(rotation);

                roomEntity.removeStatus(StatusType.LAY);
                roomEntity.removeStatus(StatusType.SIT);
                roomEntity.setNextPosition(next);

                snowStormObject.setBodyDirection(rotation);

                // Add next position if moving
                game.getExecutingEvents().add(new SnowStormAvatarMoveEvent(gamePlayer.getObjectId(), roomEntity.getNextPosition().getX(), roomEntity.getNextPosition().getY()));
            }
            else {
                roomEntity.stopWalking();
                //game.getExecutingEvents().add(new SnowStormObjectEvent((SnowStormObject) gamePlayer.getGameObject()));
            }

            // If we're walking, make sure to tell the server
            roomEntity.setNeedsUpdate(true);
        }
    }
}
