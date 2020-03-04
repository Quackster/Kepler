package org.alexdev.kepler.game.infobus;


import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabbleGame;
import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabblePlayer;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;

public class InfobusManager {
    private static InfobusManager instance;

    public static InfobusManager getInstance() {
        if (instance == null) {
            instance = new InfobusManager();
        }

        return instance;
    }

    public Infobus bus(Room room) {
        if (room == null) {
            return null;
        }

        if (!room.getTaskManager().hasTask(this.getName())) {
            return null;
        }

        return (Infobus) room.getTaskManager().getTask(this.getName());
    }

    public void addPlayer(Player player) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.getTaskManager().hasTask(this.getName())) {
            return;
        }

        Infobus infobus = (Infobus) room.getTaskManager().getTask(this.getName());
        infobus.addPlayer(player.getDetails().getId());
    }

    public void removePlayer(Player player) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.getTaskManager().hasTask(this.getName())) {
            return;
        }

        Infobus infobus = (Infobus) room.getTaskManager().getTask(this.getName());
        infobus.removePlayer(player.getDetails().getId());
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
