package net.h4bbo.kepler.game.room.tasks;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.pets.Pet;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomUserStatus;
import net.h4bbo.kepler.messages.outgoing.rooms.user.TYPING_STATUS;
import net.h4bbo.kepler.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class StatusTask implements Runnable {
    private final Room room;

    public StatusTask(Room room) {
        this.room = room;
    }

    @Override
    public void run() {
        if (this.room.getEntities().size() == 0) {
            return;
        }

        for (Entity entity : this.room.getEntities()) {
            if (entity != null) {
                if (entity.getRoomUser().getRoom() != null &&
                    entity.getRoomUser().getRoom() == this.room) {
                    this.processEntity(entity);
                }
            }
        }
    }

    /**
     * Process entity.
     *
     * @param entity the entity
     */
    private void processEntity(Entity entity) {
        List<String> toRemove = new ArrayList<>();

        if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) entity;
            player.getRoomUser().handleSpamTicks();

            processHeadRotation(player);
            processChatBubble(player);
            processPoolQueue(player);
        }

        if (entity.getType() == EntityType.PET) {
            Pet pet = (Pet) entity;

            if (pet.getRoomUser().getTask() != null)
                pet.getRoomUser().getTask().tick();
        }

        for (var kvp : entity.getRoomUser().getStatuses().entrySet()) {
            String key = kvp.getKey();
            RoomUserStatus rus = kvp.getValue();

            if (rus.getActionSwitchCountdown() > 0) {
                rus.setActionSwitchCountdown(rus.getActionSwitchCountdown() - 1);
            } else if (rus.getActionSwitchCountdown() == 0) {
                rus.setActionSwitchCountdown(-1);
                rus.setActionCountdown(rus.getSecActionSwitch());

                // Swap back to original key and update status
                rus.swapKeyAction();
                entity.getRoomUser().setNeedsUpdate(true);
            }

            if (rus.getActionCountdown() > 0) {
                rus.setActionCountdown(rus.getActionCountdown() - 1);
            } else if (rus.getActionCountdown() == 0) {
                rus.setActionCountdown(-1);
                rus.setActionSwitchCountdown(rus.getSecSwitchLifetime());

                // Swap key to action and update status
                rus.swapKeyAction();
                entity.getRoomUser().setNeedsUpdate(true);
            }

            if (rus.getLifetimeCountdown() > 0) {
                rus.setLifetimeCountdown(rus.getLifetimeCountdown() - 1);
            } else if (rus.getLifetimeCountdown() == 0) {
                rus.setLifetimeCountdown(-1);
                toRemove.add(key);

                entity.getRoomUser().setNeedsUpdate(true);
            }
        }

        for (String keyRemove : toRemove) {
            entity.getRoomUser().getStatuses().remove(keyRemove);
        }
    }

    /**
     * Make the user walk to the next tile on a pool lido queue, if they're in the diving deck and
     * they have tickets.
     *
     * @param player the player to force walking
     */
    public static void processPoolQueue(Player player) {
        if (player.getDetails().getTickets() == 0 || player.getDetails().getPoolFigure().isEmpty()) {
            return;
        }

        /*
        if (player.getRoomUser().getRoom() == null && !player.getRoomUser().getRoom().getModel().getName().equals("pool_b")) {
            return;
        }
         */

        if (player.getRoomUser().isWalking()) {
            return;
        }

        if (player.getRoomUser().getCurrentItem() != null) {
            if (player.getRoomUser().getCurrentItem().getDefinition().getSprite().equals("queue_tile2")) {
                Position front = player.getRoomUser().getCurrentItem().getPosition().getSquareInFront();
                player.getRoomUser().walkTo(front.getX(), front.getY());
            }
        }
    }

    /**
     * Check the chat bubble timer expiry.
     *
     * @param player the player to check for
     */
    public static void processChatBubble(Player player) {
        if (player.getRoomUser().getTimerManager().getChatBubbleTimer() != -1 &&
                DateUtil.getCurrentTimeSeconds() > player.getRoomUser().getTimerManager().getChatBubbleTimer()) {

            player.getRoomUser().setTyping(false);
            player.getRoomUser().getTimerManager().stopChatBubbleTimer();
            player.getRoomUser().getRoom().send(new TYPING_STATUS(player.getRoomUser().getInstanceId(), false));
        }
    }


    /**
     * Check head rotation expiry.
     * 
     * @param player the player to check for
     */
    public static void processHeadRotation(Player player) {
        if (player.getRoomUser().getTimerManager().getLookTimer() != -1 &&
                DateUtil.getCurrentTimeSeconds() > player.getRoomUser().getTimerManager().getLookTimer()) {

            player.getRoomUser().getTimerManager().stopLookTimer();
            player.getRoomUser().getPosition().setHeadRotation(player.getRoomUser().getPosition().getBodyRotation());
            player.getRoomUser().setNeedsUpdate(true);
        }
    }
}
