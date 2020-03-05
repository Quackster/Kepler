package org.alexdev.kepler.game.infobus;


import org.alexdev.kepler.dao.mysql.SettingsDao;
import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabbleGame;
import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabblePlayer;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.alexdev.kepler.util.config.writer.GameConfigWriter;
import org.apache.log4j.lf5.viewer.configure.ConfigurationManager;

public class InfobusManager {
    private static InfobusManager instance;
    private boolean isDoorOpen;
    public static InfobusManager getInstance() {
        if (instance == null) {
            instance = new InfobusManager();
        }

        return instance;
    }

    public Infobus bus() {
        Room room = RoomManager.getInstance().getRoomByModel("park_b");
        System.out.println("-----");
        System.out.println(room);
        if (room == null) {
            return null;
        }

        if (!room.getTaskManager().hasTask(this.getName())) {
            return null;
        }

        return (Infobus) room.getTaskManager().getTask(this.getName());
    }


    public boolean isDoorOpen() {
        return this.isDoorOpen;
    }

    public void openDoor(Room room) {
        room.send(new SHOWPROGRAM(new String[] { "bus", "open" }));
    }

    public void closeDoor(Room room) {
        room.send(new SHOWPROGRAM(new String[] { "bus", "close" }));
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
