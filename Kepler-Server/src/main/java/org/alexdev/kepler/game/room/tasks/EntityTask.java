package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pathfinder.Rotation;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_STATUSES;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.util.StringUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EntityTask implements Runnable {
    private final Room room;
    private final BlockingQueue<MessageComposer> queueAfterLoop;

    public EntityTask(Room room) {
        this.room = room;
        this.queueAfterLoop = new LinkedBlockingQueue<MessageComposer>();
    }

    public boolean isMoonwalkEnabled(Entity entity) {
        if (entity instanceof Player) {
            return GameConfiguration.getInstance().getBoolean("april.fools");
        }

        return false;
    }

    @Override
    public void run() {
        try {
            if (this.room.getEntities().isEmpty()) {
                return;
            }

            List<Entity> entitiesToUpdate = new ArrayList<>();

            for (Entity entity : this.room.getEntities()) {
                if (entity != null
                        && entity.getRoomUser().getRoom() != null
                        && entity.getRoomUser().getRoom() == this.room) {

                    this.processEntity(entity);

                    RoomEntity roomEntity = entity.getRoomUser();

                    if (roomEntity.isNeedsUpdate()) {
                        roomEntity.setNeedsUpdate(false);
                        entitiesToUpdate.add(entity);
                    }
                }
            }

            if (entitiesToUpdate.size() > 0) {
                this.room.send(new USER_STATUSES(entitiesToUpdate));
            }
        } catch (Exception ex) {
            Log.getErrorLogger().error("EntityTask crashed: ", ex);
        }
    }

    /**
     * Process entity.
     *
     * @param entity the entity
     */
    private void processEntity(Entity entity) {
        RoomEntity roomEntity = entity.getRoomUser();

        Position position = roomEntity.getPosition();
        Position goal = roomEntity.getGoal();

        if (roomEntity.isWalking()) {
            // Apply next tile from the tile we removed from the list the cycle before
            if (roomEntity.getNextPosition() != null) {
                Position oldPosition = roomEntity.getPosition().copy();

                roomEntity.getPosition().setX(roomEntity.getNextPosition().getX());
                roomEntity.getPosition().setY(roomEntity.getNextPosition().getY());
                roomEntity.updateNewHeight(roomEntity.getPosition());

                if (roomEntity.getCurrentItem() != null) {
                    if (roomEntity.getCurrentItem().getDefinition().getInteractionType().getTrigger() != null) {
                        roomEntity.getCurrentItem().getDefinition().getInteractionType().getTrigger().onEntityStep(entity, roomEntity, roomEntity.getCurrentItem(), oldPosition);
                    }
                }
            }

            // We still have more tiles left, so lets continue moving
            if (roomEntity.getPath().size() > 0) {
                Position next = roomEntity.getPath().pop();

                // Tile was invalid after we started walking, so lets try again!
                if (!RoomTile.isValidTile(this.room, entity, next)) {
                    entity.getRoomUser().getPath().clear();
                    this.processEntity(entity);
                    roomEntity.walkTo(goal.getX(), goal.getY());
                    return;
                }

                // Try and stop any other entities first from getting to tile
                /*var otherEntity = roomEntity.getRoom().getEntities().stream().filter(e ->
                        entity.getDetails().getId() != e.getDetails().getId() &&
                                entity.getRoomUser().getGoal().equals(e.getRoomUser().getGoal()) &&
                                e.getRoomUser().getPosition().getDistanceSquared(e.getRoomUser().getGoal()) <= 2).findFirst().orElse(null);

                if (otherEntity != null) {
                    entity.getRoomUser().getPath().clear();
                    this.processEntity(entity);
                    return;
                }*/

                RoomTile previousTile = roomEntity.getTile();

                if (previousTile != null)
                    previousTile.removeEntity(entity);

                RoomTile nextTile = roomEntity.getRoom().getMapping().getTile(next);

                if (nextTile == null) {
                    entity.getRoomUser().getPath().clear();
                    this.processEntity(entity);
                    roomEntity.walkTo(goal.getX(), goal.getY());
                    return;
                }

                nextTile.addEntity(entity);

                double newPosition = nextTile.getWalkingHeight();
                double oldPosition = position.getZ();

                if (entity.getRoomUser().getRoom().getModel().getName().startsWith("pool_") ||
                        entity.getRoomUser().getRoom().getModel().getName().equals("md_a")) {

                    int minDifference = 3;

                    if (entity.getRoomUser().getRoom().getModel().getName().equals("md_a")) {
                        minDifference = 2;
                    }

                    if ((newPosition > oldPosition) && Math.abs(newPosition - oldPosition) >= minDifference) {//(next.getZ() - roomEntity.getPosition().getZ()) > 3) {
                        if (roomEntity.containsStatus(StatusType.SWIM)) {
                            roomEntity.removeStatus(StatusType.SWIM);

                            if (roomEntity.containsStatus(StatusType.DANCE)) {
                                roomEntity.removeStatus(StatusType.DANCE);
                            }
                        }

                        if (roomEntity.containsStatus(StatusType.CARRY_DRINK)) {
                            roomEntity.removeStatus(StatusType.CARRY_DRINK);
                        }

                        if (roomEntity.containsStatus(StatusType.CARRY_ITEM)) {
                            roomEntity.removeStatus(StatusType.CARRY_ITEM);
                        }
                    }

                    if ((newPosition < oldPosition) && Math.abs(oldPosition - newPosition) >= minDifference) {
                        if (!roomEntity.containsStatus(StatusType.SWIM)) {
                            roomEntity.setStatus(StatusType.SWIM, "");

                            if (roomEntity.containsStatus(StatusType.DANCE)) {
                                roomEntity.removeStatus(StatusType.DANCE);
                            }
                        }

                        if (roomEntity.containsStatus(StatusType.CARRY_DRINK)) {
                            roomEntity.removeStatus(StatusType.CARRY_DRINK);
                        }

                        if (roomEntity.containsStatus(StatusType.CARRY_ITEM)) {
                            roomEntity.removeStatus(StatusType.CARRY_ITEM);
                        }
                    }
                }

                // Set up trigger for leaving a current item
                if (roomEntity.getLastItemInteraction() != null) {
                    if (roomEntity.getLastItemInteraction().getDefinition().getInteractionType().getTrigger() != null) {
                        roomEntity.getLastItemInteraction().getDefinition().getInteractionType().getTrigger().onEntityLeave(entity, roomEntity, roomEntity.getCurrentItem());
                    }

                    roomEntity.setLastItemInteraction(null);
                }

                roomEntity.removeStatus(StatusType.LAY);
                roomEntity.removeStatus(StatusType.SIT);

                int rotation = isMoonwalkEnabled(entity) ?
                        Rotation.calculateWalkDirection(next.getX(), next.getY(), position.getX(), position.getY()) :
                        Rotation.calculateWalkDirection(position.getX(), position.getY(), next.getX(), next.getY());

                double height = nextTile.getWalkingHeight();

                roomEntity.getPosition().setRotation(rotation);
                roomEntity.setStatus(StatusType.MOVE, next.getX() + "," + next.getY() + "," + StringUtil.format(height));
                roomEntity.setNextPosition(next);
            } else {
                roomEntity.stopWalking();
            }

            // If we're walking, make sure to tell the server
            roomEntity.setNeedsUpdate(true);
        }
    }
}
