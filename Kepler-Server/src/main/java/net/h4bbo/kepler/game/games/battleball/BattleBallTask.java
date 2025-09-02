package net.h4bbo.kepler.game.games.battleball;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameEvent;
import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallPlayerState;
import net.h4bbo.kepler.game.games.battleball.events.PlayerMoveEvent;
import net.h4bbo.kepler.game.games.battleball.objects.PlayerUpdateObject;
import net.h4bbo.kepler.game.games.battleball.objects.PowerUpUpdateObject;
import net.h4bbo.kepler.game.games.enums.GameState;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.player.GameTeam;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.pathfinder.Rotation;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.entities.RoomEntity;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.game.room.mapping.RoomTile;
import net.h4bbo.kepler.log.Log;
import net.h4bbo.kepler.messages.outgoing.games.GAMESTATUS;
import net.h4bbo.kepler.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class BattleBallTask implements Runnable {
    private final Room room;
    private final BattleBallGame game;

    public BattleBallTask(Room room, BattleBallGame game) {
        this.room = room;
        this.game = game;
    }

    @Override
    public void run() {
        try {
            if (this.game.getActivePlayers().isEmpty() || this.game.getGameState() == GameState.ENDED) {
                return; // Don't send any packets or do any logic checks during when the game is finished
            }

            List<GameObject> objects = new ArrayList<>();
            List<GameEvent> events = new ArrayList<>();

            List<BattleBallTile> updateTiles = new ArrayList<>();
            List<BattleBallTile> fillTiles = new ArrayList<>();

            this.game.getEventsQueue().drainTo(events);
            this.game.getObjectsQueue().drainTo(objects);
            this.game.getUpdateTilesQueue().drainTo(updateTiles);
            this.game.getFillTilesQueue().drainTo(fillTiles);
            this.game.getTeams().values().forEach(GameTeam::calculateScore);

            for (GamePlayer gamePlayer : this.game.getActivePlayers()) {
                Player player = gamePlayer.getPlayer();

                if (player.getRoomUser().getRoom() != this.room) {
                    continue;
                }

                if (gamePlayer.getPlayerState() == BattleBallPlayerState.CLIMBING_INTO_CANNON ||
                        gamePlayer.getPlayerState() == BattleBallPlayerState.FLYING_THROUGH_AIR) {
                    continue;
                }

                if (this.game.getStoredPowers().containsKey(gamePlayer)) {
                    for (BattleBallPowerUp powerUp : this.game.getStoredPowers().get(gamePlayer)) {
                        objects.add(new PowerUpUpdateObject(powerUp));
                    }
                }

                player.getRoomUser().handleSpamTicks();
                this.processEntity(gamePlayer, objects, events, updateTiles, fillTiles);

                objects.add(new PlayerUpdateObject(gamePlayer));
            }

            this.game.send(new GAMESTATUS(this.game, this.game.getTeams().values(), objects, events, updateTiles, fillTiles));
        } catch (Exception ex) {
            Log.getErrorLogger().error("GameTask crashed: ", ex);
        }
    }

    /**
     * Process entity.
     */
    private void processEntity(GamePlayer gamePlayer, List<GameObject> objects, List<GameEvent> events, List<BattleBallTile> updateTiles, List<BattleBallTile> fillTiles) {
        Entity entity = (Entity) gamePlayer.getPlayer();
        Game game = gamePlayer.getGame();

        RoomEntity roomEntity = entity.getRoomUser();

        Position position = roomEntity.getPosition();
        Position goal = roomEntity.getGoal();

        if (roomEntity.isWalking()) {
            // Apply next tile from the tile we removed from the list the cycle before
            if (roomEntity.getNextPosition() != null) {
                /*roomEntity.getPosition().setX(roomEntity.getNextPosition().getX());
                roomEntity.getPosition().setY(roomEntity.getNextPosition().getY());
                roomEntity.updateNewHeight(roomEntity.getPosition());*/
                /*RoomTile nextTile = roomEntity.getRoom().getMapping().getTile(roomEntity.getNextPosition());
                boolean isRedirected = false;
                if (nextTile.getEntities().size() > 0 && !nextTile.containsEntity(entity)) {
                    roomEntity.setNextPosition(roomEntity.getPosition().copy());
                    isRedirected = true;
                }*/

                roomEntity.getPosition().setX(roomEntity.getNextPosition().getX());
                roomEntity.getPosition().setY(roomEntity.getNextPosition().getY());
                roomEntity.updateNewHeight(roomEntity.getPosition());

                if (roomEntity.getNextPosition() != null) {
                    RoomTile nextTile = roomEntity.getRoom().getMapping().getTile(roomEntity.getNextPosition());

                    if (nextTile != null && nextTile.getOtherEntities(entity).size() < 1) {
                        BattleBallTile tile = (BattleBallTile) game.getTile(roomEntity.getNextPosition().getX(), roomEntity.getNextPosition().getY());

                        if (tile != null) {
                            tile.interact(gamePlayer, objects, events, updateTiles, fillTiles);
                        }
                    }
                }
            }

            // We still have more tiles left, so lets continue moving
            if (roomEntity.getPath().size() > 0) {
                Position next = roomEntity.getPath().pop();

                RoomTile previousTile = roomEntity.getTile();
                RoomTile nextTile = roomEntity.getRoom().getMapping().getTile(next);

                // Tile was invalid after we started walking, so lets try again!
                if (nextTile == null || !RoomTile.isValidTile(this.room, entity, next) && !next.equals(goal)) {
                    entity.getRoomUser().getPath().clear();
                    roomEntity.walkTo(goal.getX(), goal.getY());
                    this.processEntity(gamePlayer, objects, events, updateTiles, fillTiles);
                    return;
                }

                if (previousTile != null) {
                    previousTile.removeEntity(entity);
                }

                nextTile.addEntity(entity);

                roomEntity.removeStatus(StatusType.LAY);
                roomEntity.removeStatus(StatusType.SIT);

                int rotation = Rotation.calculateWalkDirection(position.getX(), position.getY(), next.getX(), next.getY());
                double height = nextTile.getWalkingHeight();

                roomEntity.getPosition().setRotation(rotation);
                roomEntity.setStatus(StatusType.MOVE, next.getX() + "," + next.getY() + "," + StringUtil.format(height));
                roomEntity.setNextPosition(next);

                // Add next position if moving
                events.add(new PlayerMoveEvent(gamePlayer, roomEntity.getNextPosition().copy()));
            } else {
                roomEntity.stopWalking();

                RoomTile previousTile = roomEntity.getRoom().getMapping().getTile(roomEntity.getPosition());
                RoomTile nextTile = roomEntity.getRoom().getMapping().getTile(roomEntity.getPosition().getSquareBehind());

                // If an entity exists on the tile, push them back
                if (previousTile != null && !(previousTile.getOtherEntities(entity).size() < 1)) {
                    previousTile.removeEntity(entity);
                    nextTile.addEntity(entity);

                    // Set new position
                    entity.getRoomUser().setPosition(roomEntity.getPosition().getSquareBehind());
                }
            }

            // If we're walking, make sure to tell the server
            roomEntity.setNeedsUpdate(true);
        }
    }
}