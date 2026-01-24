package net.h4bbo.kepler.game.room.handlers.walkways;

import net.h4bbo.kepler.dao.mysql.PublicRoomsDao;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.room.Room;

import java.util.ArrayList;
import java.util.List;

public class WalkwaysManager {
    private static WalkwaysManager instance;
    private List<WalkwaysEntrance> walkways;

    public WalkwaysManager() {
        this.walkways = PublicRoomsDao.getWalkways();
    }

    public static WalkwaysEntrance createWalkway(int roomId, int roomTargetId, String fromCoords, String destination) {
        List<Position> coordinates = new ArrayList<>();

        for (String coord : fromCoords.split(" ")) {
            int x = Integer.parseInt(coord.split(",")[0]);
            int y = Integer.parseInt(coord.split(",")[1]);
            coordinates.add(new Position(x, y));
        }

        Position destinationPosition = null;

        if (destination != null) {
            String[] data = destination.split(",");
            int x = Integer.parseInt(data[0]);
            int y = Integer.parseInt(data[1]);
            int z = Integer.parseInt(data[2]);
            int rotation = Integer.parseInt(data[3]);
            destinationPosition = new Position(x, y, z, rotation, rotation);
        }

        return new WalkwaysEntrance(roomId, roomTargetId, coordinates, destinationPosition);
    }

    public WalkwaysEntrance getDestination(Room room, Position position) {
        if (!room.isPublicRoom()) {
            return null;
        }

        for (WalkwaysEntrance entrance : this.walkways) {
            if (entrance.getRoomId() != room.getId()) {
                continue;
            }

            Position destination = room.getModel().getDoorLocation();

            if (entrance.getDestination() != null) {
                destination = entrance.getDestination();
            }

            if (destination.equals(position)) {
                return entrance;
            }
        }

        return null;
    }

    public WalkwaysEntrance getWalkway(Room room, Position position) {
        if (!room.isPublicRoom()) {
            return null;
        }

        for (WalkwaysEntrance entrance : this.walkways) {
            if (entrance.getRoomId() != room.getId()) {
                continue;
            }

            for (Position coord : entrance.getFromCoords()) {
                if (coord.equals(position)) {
                    return entrance;
                }
            }
        }

        return null;
    }

    /**
     * Get walkways.
     *
     * @return the list of walkways
     */
    public List<WalkwaysEntrance> getWalkways() {
        return walkways;
    }

    /**
     * Get the {@link WalkwaysManager} instance
     *
     * @return the item manager instance
     */
    public static WalkwaysManager getInstance() {
        if (instance == null) {
            instance = new WalkwaysManager();
        }

        return instance;
    }
}
