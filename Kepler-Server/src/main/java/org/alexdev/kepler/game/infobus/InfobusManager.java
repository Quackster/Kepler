package org.alexdev.kepler.game.infobus;


import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabbleGame;
import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabblePlayer;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;

public class InfobusManager {
    private static InfobusManager instance;

    public static InfobusManager getInstance() {
        if (instance == null) {
            instance = new InfobusManager();
        }

        return instance;
    }

    public Infobus bus() {
        Room room = RoomManager.getInstance().getRoomByModel("park_b");
        if (room == null) {
            return null;
        }

        if (!room.getTaskManager().hasTask(this.getName())) {
            return null;
        }

        return (Infobus) room.getTaskManager().getTask(this.getName());
    }


    /**
     * Gets the name of the infobus task.
     *
     * @return the game task
     */
    public String getName() {
        return "InfobusTask";
    }
}
