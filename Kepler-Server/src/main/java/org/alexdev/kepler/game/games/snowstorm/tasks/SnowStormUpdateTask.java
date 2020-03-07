package org.alexdev.kepler.game.games.snowstorm.tasks;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.games.enums.GameState;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormObject;
import org.alexdev.kepler.game.games.snowstorm.SnowStormTurn;
import org.alexdev.kepler.game.games.snowstorm.events.SnowStormAvatarMoveEvent;
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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SnowStormUpdateTask implements Runnable {
    private final Room room;
    private final SnowStormGame game;
    private List<SnowStormTurn> snowStormTurnList;
    private int maxGameTurns = 5;

    public SnowStormUpdateTask(Room room, SnowStormGame game) {
        this.room = room;
        this.game = game;
        this.resetTurns();
    }

    private void resetTurns() {
        this.snowStormTurnList = new CopyOnWriteArrayList<>();

        for (int i = 0; i < maxGameTurns; i++) {
            this.snowStormTurnList.add(new SnowStormTurn());
        }
    }

    @Override
    public void run() {
        try {
            if (!this.game.isGameStarted() || this.game.getGameState() == GameState.ENDED || this.game.getPlayers().isEmpty()) {
                return; // Don't send any packets or do any logic checks during when the game is finished
            }

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
                    }
                }
            }

        } catch (Exception ex) {
            Log.getErrorLogger().error("SnowstormWalkTask crashed: ", ex);
        }

        try {
            if (this.snowStormTurnList.stream().anyMatch(turn -> turn.getSubTurns().size() > 0)) {
                for (Player player : this.game.getRoom().getEntityManager().getPlayers()) {
                    player.send(new SNOWSTORM_GAMESTATUS(this.snowStormTurnList));
                }

                this.resetTurns();
            }

        } catch (Exception ex) {
            Log.getErrorLogger().error("SnowstormTask crashed: ", ex);

        }
    }

    /**
     * Process entity.
     */
    private void processEntity(GamePlayer gamePlayer, SnowStormGame game) {
        int walkingGameTurn = 5;
        Entity entity = gamePlayer.getPlayer();
        RoomEntity roomEntity = gamePlayer.getPlayer().getRoomUser();

        SnowStormAvatarObject snowStormObject = (SnowStormAvatarObject) gamePlayer.getGameObject();

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
                this.queueSubTurn(walkingGameTurn, new SnowStormAvatarMoveEvent(gamePlayer.getObjectId(), roomEntity.getNextPosition().getX(), roomEntity.getNextPosition().getY()));
                //this.queueSubTurn(4, new SnowStormAvatarMoveEvent(gamePlayer.getObjectId(), roomEntity.getNextPosition().getX(), roomEntity.getNextPosition().getY()));
            } else {
                //this.queueSubTurn(walkingGameTurn, new SnowStormAvatarMoveEvent(gamePlayer.getObjectId(), roomEntity.getNextPosition().getX(), roomEntity.getNextPosition().getY()));
                roomEntity.stopWalking();
                this.queueSubTurn(walkingGameTurn, new SnowStormAvatarMoveEvent(gamePlayer.getObjectId(), roomEntity.getPosition().getX(), roomEntity.getPosition().getY()));
            }

            // If we're walking, make sure to tell the server
            roomEntity.setNeedsUpdate(true);
        }
    }

    public void queueSubTurn(int frame, SnowStormObject gameEvent) {
        try {
            this.snowStormTurnList.get(frame - 1).getSubTurns().add(gameEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<SnowStormTurn> getExecutingTurns() {
        return snowStormTurnList;
    }
}
