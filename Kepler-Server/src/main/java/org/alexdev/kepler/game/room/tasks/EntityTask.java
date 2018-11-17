package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pathfinder.Rotation;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_STATUSES;
import org.alexdev.kepler.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class EntityTask implements Runnable {
    private final Room room;

    public EntityTask(Room room) {
        this.room = room;
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
                    if (roomEntity.getCurrentItem().getItemTrigger() != null) {
                        roomEntity.getCurrentItem().getItemTrigger().onEntityStep(entity, roomEntity, roomEntity.getCurrentItem(), oldPosition);
                    }
                }
            }

            // We still have more tiles left, so lets continue moving
            if (roomEntity.getPath().size() > 0) {
                Position next = roomEntity.getPath().pop();

                // Tile was invalid after we started walking, so lets try again!
                if (!RoomTile.isValidTile(this.room, entity, next)) {
                    entity.getRoomUser().getPath().clear();
                    roomEntity.walkTo(goal.getX(), goal.getY());
                    this.processEntity(entity);
                    return;
                }

                // Set up trigger for leaving a current item
                if (roomEntity.getCurrentItem() != null) {
                    if (roomEntity.getCurrentItem().getItemTrigger() != null) {
                        roomEntity.getCurrentItem().getItemTrigger().onEntityLeave(entity, roomEntity, roomEntity.getCurrentItem());
                    }
                }

                RoomTile previousTile = roomEntity.getTile();
                previousTile.removeEntity(entity);

                RoomTile nextTile = roomEntity.getRoom().getMapping().getTile(next);
                nextTile.addEntity(entity);

                roomEntity.removeStatus(StatusType.LAY);
                roomEntity.removeStatus(StatusType.SIT);

                int rotation = Rotation.calculateWalkDirection(position.getX(), position.getY(), next.getX(), next.getY());
                double height = this.room.getMapping().getTile(next).getWalkingHeight();

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
