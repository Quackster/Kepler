package org.alexdev.kepler.game.room.models.triggers;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.triggers.GenericTrigger;
import org.alexdev.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class DivingDeckTrigger extends GenericTrigger {
    public static class PoolCamera implements Runnable {
        private final Room room;
        private Player player;
        private int cameraType;

        public PoolCamera(Room room) {
            this.room = room;
        }

        @Override
        public void run() {
            if (this.player == null) {
                this.spectateNewPlayer();
                this.newCameraMode(-1);
                return;
            }

            int cameraType = ThreadLocalRandom.current().nextInt(0, 3);

            switch (cameraType) {
                case 0: {
                    this.spectateNewPlayer();
                    break;
                }
                case 1: {
                    this.newCameraMode(1);
                    break;
                }
                case 2: {
                    this.newCameraMode(2);
                    break;
                }
            }
        }

        /**
         * Finds a new player to spectate on the camera.
         */
        public void spectateNewPlayer() {
            List<Player> playerList = this.room.getEntityManager().getPlayers();

            if (playerList.size() > 1) {
                Player found = playerList.get(ThreadLocalRandom.current().nextInt(0, playerList.size()));

                if (found.getDetails().getId() == this.player.getDetails().getId()) {
                    spectateNewPlayer();
                    return;
                }

                this.player = found;
            } else {
                this.player = playerList.get(0);
            }

            this.room.send(new SHOWPROGRAM(new String[]{"cam1", "targetcamera", String.valueOf(this.player.getRoomUser().getInstanceId())}));
        }

        /**
         * Creates a new camera mode for the camera and sends it to all the users.
         */
        public void newCameraMode(int mode) {
            this.cameraType = mode > 0 ? mode : ThreadLocalRandom.current().nextInt(1, 3);
            this.room.send(new SHOWPROGRAM(new String[]{"cam1", "setcamera", String.valueOf(this.cameraType)}));
        }

        /**
         * Gets the current active player being spectated
         *
         * @return the player being spectated
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * Get the camera type (zoomed in or zoomed out)
         *
         * @return the camera type
         */
        public int getCameraType() {
            return cameraType;
        }
    }

    @Override
    public void onRoomEntry(Entity entity, Room room, Object... customArgs) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player)entity;

        if (room.getTaskManager().hasTask("DivingCamera")) {
            PoolCamera task = (PoolCamera) room.getTaskManager().getTask("DivingCamera");
            player.send(new SHOWPROGRAM(new String[]{"cam1", "targetcamera", String.valueOf(task.getPlayer().getRoomUser().getInstanceId())}));
            player.send(new SHOWPROGRAM(new String[]{"cam1", "setcamera", String.valueOf(task.getCameraType())}));
        } else {
            room.getTaskManager().scheduleTask("DivingCamera", new PoolCamera(room), 0, 10, TimeUnit.SECONDS);
        }

        if (player.getRoomUser().getPosition().getZ() == 1.0) { // User entered room from the other pool
            player.getRoomUser().setStatus(StatusType.SWIM, "");
            player.getRoomUser().setNeedsUpdate(true);
        }
    }

    @Override
    public void onRoomLeave(Entity entity, Room room, Object... customArgs)  {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        if (room.getEntityManager().getPlayers().isEmpty()) {
            return;
        }

        Player player = (Player)entity;

        DivingDeckTrigger.PoolCamera task = (DivingDeckTrigger.PoolCamera) room.getTaskManager().getTask("DivingCamera");

        if (task.getPlayer() == player) {
            task.spectateNewPlayer();
        }
    }
}
