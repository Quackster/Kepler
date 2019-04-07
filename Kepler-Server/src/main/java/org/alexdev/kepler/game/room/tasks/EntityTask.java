package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.pets.Pet;
import org.alexdev.kepler.game.pets.PetManager;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pathfinder.Rotation;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_STATUSES;
import org.alexdev.kepler.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

                    if (entity.getType() == EntityType.PET) {
                        this.processPet((Pet) entity);
                    }

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
    public void processEntity(Entity entity) {
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
                    roomEntity.walkTo(goal.getX(), goal.getY());
                    this.processEntity(entity);
                    return;
                }

                // Set up trigger for leaving a current item
                if (roomEntity.getCurrentItem() != null) {
                    if (roomEntity.getCurrentItem().getDefinition().getInteractionType().getTrigger() != null) {
                        roomEntity.getCurrentItem().getDefinition().getInteractionType().getTrigger().onEntityLeave(entity, roomEntity, roomEntity.getCurrentItem());
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

                double newPosition =  this.room.getMapping().getTile(next).getWalkingHeight();
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

                            if (previousTile.getHighestItem() != null &&
                                    (previousTile.getHighestItem().getDefinition().getSprite().equals("poolExit") || previousTile.getHighestItem().getDefinition().getSprite().equals("poolExit"))) {
                                previousTile.getHighestItem().showProgram(null);
                            }
                        }
                    }

                    if ((newPosition < oldPosition) && Math.abs(oldPosition - newPosition) >= minDifference) {
                        if (!roomEntity.containsStatus(StatusType.SWIM)) {
                            roomEntity.setStatus(StatusType.SWIM, "");

                            if (roomEntity.containsStatus(StatusType.DANCE)) {
                                roomEntity.removeStatus(StatusType.DANCE);
                            }

                            if (nextTile.getHighestItem() != null &&
                                    (nextTile.getHighestItem().getDefinition().getSprite().equals("poolExit") || nextTile.getHighestItem().getDefinition().getSprite().equals("poolExit"))) {
                                nextTile.getHighestItem().showProgram(null);
                            }
                        }
                    }
                }


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

    /**
     * Process pet actions.
     *
     * @param pet the pet to process
     */
    private void processPet(Pet pet) {
        switch (ThreadLocalRandom.current().nextInt(0, 30)) {
            case 1: {
                Position availableTile = this.room.getMapping().getRandomWalkableBound(pet);

                if (availableTile != null) {
                    pet.getRoomUser().walkTo(availableTile.getX(), availableTile.getY());
                }

                break;
            }
            case 2: {
                if (ThreadLocalRandom.current().nextInt(0, 3) == 0) {
                    pet.getRoomUser().talk(PetManager.getInstance().getRandomSpeech(pet.getDetails().getType()), CHAT_MESSAGE.ChatMessageType.CHAT);
                }
                break;
            }
        }
    }
}
