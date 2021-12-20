package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pathfinder.Rotation;
import org.alexdev.kepler.game.pets.Pet;
import org.alexdev.kepler.game.pets.PetAction;
import org.alexdev.kepler.game.pets.PetManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_STATUSES;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.util.StringUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

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

                    if (entity.getType() == EntityType.PET) {
                        this.processPet((Pet)entity);
                    }

                    if (entity.getType() == EntityType.PLAYER) {
                        ((Player)entity).getRoomUser().handleSpamTicks();
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

            this.handleMessageQueue();
        } catch (Exception ex) {
            Log.getErrorLogger().error("EntityTask crashed: ", ex);
        }
    }

    /**
     * Handles messages to be queued after the main room loop was sent
     */
    private void handleMessageQueue() {
        try {
            List<MessageComposer> queueSending = new ArrayList<>();
            this.queueAfterLoop.drainTo(queueSending);

            for (MessageComposer messageComposer : queueSending) {
                this.room.send(messageComposer);
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

    /**
     * Process pet actions.
     *
     * @param pet the pet to process
     */
    private void processPet(Pet pet) {
        if (pet.hasActionExpired() && pet.getAction() != PetAction.NONE) {
            if (pet.getAction() == PetAction.SLEEP) {
                pet.awake();
            }

            pet.getRoomUser().getStatuses().clear();
            pet.getRoomUser().setNeedsUpdate(true);
            pet.setAction(PetAction.NONE);
        } else {
            switch (ThreadLocalRandom.current().nextInt(0, 6)) {
                case 1: {
                    pet.getRoomUser().removeStatus(StatusType.SIT);
                    pet.getRoomUser().removeStatus(StatusType.LAY);
                    pet.getRoomUser().removeStatus(StatusType.PET_SLEEP);

                    if (pet.getRoomUser().containsStatus(StatusType.EAT) ||
                            pet.getRoomUser().containsStatus(StatusType.DEAD) ||
                            pet.getRoomUser().containsStatus(StatusType.JUMP)) {
                        return;
                    }

                    if (pet.isDoingAction()) {
                        return;
                    }

                    switch (ThreadLocalRandom.current().nextInt(0, 8)) {
                        case 0: {
                            if (pet.isThirsty()) {
                                pet.getRoomUser().tryDrinking();
                                return;
                            }
                            break;
                        }
                        case 1: {
                            if (pet.isHungry()) {
                                pet.getRoomUser().tryEating();
                                return;
                            }
                            break;
                        }
                    }


                    Position availableTile = this.room.getMapping().getRandomWalkableBound(pet);

                    if (availableTile != null) {
                        pet.getRoomUser().walkTo(availableTile.getX(), availableTile.getY());
                    }

                    break;
                }
            }

            if (!pet.isWalkBeforeSitLay() && pet.isActionAllowed()) {
                switch (ThreadLocalRandom.current().nextInt(0, 15)) {
                    case 1: {
                        if (!pet.getRoomUser().isWalking() && pet.getAction() == PetAction.NONE) {
                            pet.getRoomUser().getPosition().setRotation(pet.getRoomUser().getPosition().getBodyRotation());
                            pet.getRoomUser().setStatus(StatusType.SIT, StringUtil.format(pet.getRoomUser().getPosition().getZ()));
                            pet.setWalkBeforeSitLay(true);

                            pet.setAction(PetAction.SIT);
                            pet.setActionDuration(ThreadLocalRandom.current().nextInt(15, 30));

                            if (ThreadLocalRandom.current().nextInt(0, 3) == 0) {
                                List<Player> playerList = this.room.getEntityManager().getEntitiesByClass(Player.class);
                                playerList.sort(Comparator.comparingInt(p -> p.getRoomUser().getPosition().getDistanceSquared(pet.getRoomUser().getPosition())));

                                if (playerList.size() > 0) {
                                    pet.getRoomUser().getPosition().setRotation(Rotation.calculateWalkDirection(
                                            pet.getRoomUser().getPosition(),
                                            playerList.get(0).getRoomUser().getPosition()));
                                }
                            }

                            pet.getRoomUser().setNeedsUpdate(true);
                        }
                        break;
                    }
                    case 2: {
                        if (!pet.getRoomUser().isWalking() && pet.getAction() == PetAction.NONE) {
                            pet.getRoomUser().getPosition().setRotation(pet.getRoomUser().getPosition().getBodyRotation());
                            pet.getRoomUser().setStatus(StatusType.LAY, StringUtil.format(pet.getRoomUser().getPosition().getZ()) + " null");
                            pet.setWalkBeforeSitLay(true);

                            pet.setAction(PetAction.LAY);
                            pet.setActionDuration(ThreadLocalRandom.current().nextInt(15, 30));

                            if (ThreadLocalRandom.current().nextInt(0, 5) == 0) {
                                List<Player> playerList = this.room.getEntityManager().getEntitiesByClass(Player.class);
                                playerList.sort(Comparator.comparingInt(p -> p.getRoomUser().getPosition().getDistanceSquared(pet.getRoomUser().getPosition())));

                                if (playerList.size() > 0) {
                                    pet.getRoomUser().getPosition().setRotation(Rotation.calculateWalkDirection(
                                            pet.getRoomUser().getPosition(),
                                            playerList.get(0).getRoomUser().getPosition()));
                                }
                            }

                            pet.getRoomUser().setNeedsUpdate(true);
                        }
                        break;
                    }
                }
            }

            if (pet.getAction() == PetAction.SIT || pet.getAction() == PetAction.LAY || pet.getAction() == PetAction.NONE) {
                if (ThreadLocalRandom.current().nextInt(0, 8) == 0) {
                    if (!pet.getRoomUser().isWalking()) {
                        List<Player> playerList = this.room.getEntityManager().getEntitiesByClass(Player.class);
                        playerList.sort(Comparator.comparingInt(p -> p.getRoomUser().getPosition().getDistanceSquared(pet.getRoomUser().getPosition())));

                        if (playerList.size() > 0) {
                            pet.getRoomUser().getPosition().setHeadRotation(Rotation.getHeadRotation(
                                    pet.getRoomUser().getPosition().getRotation(),
                                    pet.getRoomUser().getPosition(),
                                    playerList.get(0).getRoomUser().getPosition()));
                            pet.getRoomUser().setNeedsUpdate(true);
                        }
                    }
                }
            }
        }

        switch (ThreadLocalRandom.current().nextInt(0, 30)) {
            case 2: {
                if (ThreadLocalRandom.current().nextInt(0, 8) == 0) {
                    pet.getRoomUser().talk(PetManager.getInstance().getRandomSpeech(pet.getDetails().getType()), CHAT_MESSAGE.ChatMessageType.CHAT);
                }
                break;
            }
        }
    }


    /**
     * Used for sending packets after the loop has completed.
     *
     * @return the queue to add composers into
     */
    public BlockingQueue<MessageComposer> getQueueAfterLoop() {
        return queueAfterLoop;
    }
}
