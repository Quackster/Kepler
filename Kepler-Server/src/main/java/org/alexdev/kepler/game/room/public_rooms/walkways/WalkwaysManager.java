package org.alexdev.kepler.game.room.public_rooms.walkways;

import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalkwaysManager {
    private static WalkwaysManager instance;
    private static Map<String, String> walkwayMap = new HashMap<>() {{
        put("rooftop_2", "rooftop");
        put("old_skool1", "old_skool0");
        put("malja_bar_b", "malja_bar_a");
        put("bar_b", "bar_a");
        put("pool_b", "pool_a");

        put("hallway0", "hallway2");
        put("hallway1", "hallway2");
        put("hallway3", "hallway2");
        put("hallway4", "hallway2");
        put("hallway5", "hallway2");

        put("hallway6", "hallway9");
        put("hallway7", "hallway9");
        put("hallway8", "hallway9");
        put("hallway10", "hallway9");
        put("hallway11", "hallway9");

        put("hallA", "entryhall");
        put("hallB", "entryhall");
        put("hallC", "entryhall");
        put("hallD", "entryhall");

        put("gate_park_2", "gate_park");

        put("park_b", "park_b");
    }};

    private List<WalkwaysEntrance> walkways;

    public WalkwaysManager() {
        this.walkways = new ArrayList<>();

        this.addWalkway("park_a", "park_b", "28,4", null);
        this.addWalkway("park_b", "park_a", "11,2", "28,5,0,4");

        this.addWalkway("gate_park", "gate_park_2", "23,0 22,0 20,0 19,0 18,0 17,0 16,0 15,0 14,0 11,0 10,0 9,0", null);
        this.addWalkway("gate_park_2", "gate_park", "16,24 15,24 17,24 18,24 18,25 17,25 16,25 15,25 18,26 17,26 16,26", "16,2,2,4");

        this.addWalkway("rooftop", "rooftop_2", "9,4 10,4 9,3", null);
        this.addWalkway("rooftop_2", "rooftop", "3,11 4,11 5,11", "10,5,4,4");

        this.addWalkway("old_skool0", "old_skool1", "16,18", null);
        this.addWalkway("old_skool1", "old_skool0", "0,7", "15,18,0,6");

        this.addWalkway("malja_bar_a", "malja_bar_b", "14,0 15,0", null);
        this.addWalkway("malja_bar_b", "malja_bar_a", "5,25 ", "15,1,4,4");

        this.addWalkway("bar_a", "bar_b", "9,32 10,32 11,32 9,33 10,33", null);
        this.addWalkway("bar_b", "bar_a", "1,10 1,11 1,12", "10,30,5,0");

        this.addWalkway("pool_a", "pool_b", "19,3 20,4 21,5 22,6 23,7 24,8 25,9 26,10 27,11 28,12", null);
        this.addWalkway("pool_b", "pool_a", "0,13 1,14 2,15 3,16 4,17 5,18 6,19 7,20 8,21 9,22 10,23 11,24 12,25", "23,7,7,5");

        this.addWalkway("pool_a", "pool_b", "30,14 31,15 32,16 33,17 34,18 35,19 36,20 37,21 38,22 39,23", "18,30,1,1");
        this.addWalkway("pool_b", "pool_a", "13,26 14,27 15,28 16,29 17,30 18,31 19,32 20,33 21,34", "34,19,1,5");


        // Lower Hallways
        this.addWalkway("hallway2", "hallway0", "0,6 0,7 0,8 0,9", "29,3,1,6");
        this.addWalkway("hallway2", "hallway3", "6,23 7,23 8,23 9,23", "7,2,1,4");
        this.addWalkway("hallway2", "hallway4", "27,6 27,7 27,8 27,9", "2,3,0,2");
        this.addWalkway("hallway0", "hallway2", "31,5 31,4 31,3 31,2", "2,7,1,2");
        this.addWalkway("hallway0", "hallway1", "14,19 15,19 16,19 17,19", "15,2,0,4");
        this.addWalkway("hallway1", "hallway3", "31,9 31,8 31,7 31,6", "2,8,1,2");
        this.addWalkway("hallway1", "hallway0", "17,0 16,0 15,0 14,0", "16,17,1,0");
        this.addWalkway("hallway3", "hallway2", "9,0 8,0 7,0 6,0", "8,21,1,0");
        this.addWalkway("hallway3", "hallway1", "0,9 0,8 0,7 0,6", "29,7,0,6");
        this.addWalkway("hallway3", "hallway5", "31,6 31,7 31,8 31,9", "2,15,0,2");
        this.addWalkway("hallway5", "hallway3", "0,17 0,16 0,15 0,14", "29,7,0,6");
        this.addWalkway("hallway5", "hallway4", "22,0 23,0 24,0 25,0", "24,17,1,0");
        this.addWalkway("hallway4", "hallway2", "0,2 0,3 0,4 0,5", "25,7,0,6");
        this.addWalkway("hallway4", "hallway5", "22,19 23,19 24,19 25,19", "24,2,1,4");

        // Upper Hallways
        this.addWalkway("hallway9", "hallway8", "14,0 15,0 16,0 17,0", "19,21,0,0");
        this.addWalkway("hallway9", "hallway10", "14,31 15,31 16,31 17,31", "3,6,1,4");
        this.addWalkway("hallway9", "hallway7", "0,14 0,15 0,16 0,17", "17,23,0,6");
        this.addWalkway("hallway9", "hallway11", "31,17 31,16 31,15 31,14", "2,3,1,2");
        this.addWalkway("hallway8", "hallway7", "0,14 0,15 0,16 0,17", "13,8,1,6");
        this.addWalkway("hallway8", "hallway9", "18,23 19,23 20,23 21,23", "16,2,0,4");
        this.addWalkway("hallway7", "hallway8", "15,6 15,7 15,8 15,9", "2,15,1,2");
        this.addWalkway("hallway7", "hallway6", "0,25 0,24 0,23 0,22", "21,12,0,6");
        this.addWalkway("hallway7", "hallway9", "19,22 19,23 19,24 19,25", "2,15,0,2");
        this.addWalkway("hallway6", "hallway7", "23,13 23,12 23,11 23,10", "2,23,0,2");
        this.addWalkway("hallway10", "hallway9", "2,4 3,4 4,4 5,4", "15,29,0,0");
        this.addWalkway("hallway10", "hallway11", "17,0 17,1 17,2 17,3", "10,19,0,2");
        this.addWalkway("hallway11", "hallway10", "8,18 8,19 8,20 8,21", "15,1,0,6");
        this.addWalkway("hallway11", "hallway9", "0,5 0,4 0,3 0,2", "29,15,0,6");

        // Cunning Fox Gamehall
        this.addWalkway("entryhall", "hallA", "2,0 3,0", "1,1,1,4");
        this.addWalkway("entryhall", "hallB", "8,0 9,0", "2,1,1,4");
        this.addWalkway("entryhall", "hallC", "14,0 15,0", "1,1,1,4");
        this.addWalkway("entryhall", "hallD", "0,2 0,3", "1,1,1,4");

        this.addWalkway("hallA", "entryhall", "0,0 1,0", "3,1,1,4");
        this.addWalkway("hallB", "entryhall", "2,0 1,0", "9,1,1,4");
        this.addWalkway("hallC", "entryhall", "0,0 1,0", "15,1,1,4");
        this.addWalkway("hallD", "entryhall", "0,0 1,0", "1,3,1,2");
        //this.addWalkway("ha", "rooftop", "3,11 4,11 5,11", "10,5,4,4");
    }

    private void addWalkway(String modelFrom, String modelTo, String fromCoords, String destination) {
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

        this.walkways.add(new WalkwaysEntrance(modelFrom, modelTo, coordinates, destinationPosition));
    }

    public WalkwaysEntrance getDestination(Room room, Position position) {
        if (!room.isPublicRoom()) {
            return null;
        }

        for (WalkwaysEntrance entrance : this.walkways) {
            if (!entrance.getModelTo().equals(room.getModel().getName())) {
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
            if (!entrance.getModelFrom().equals(room.getModel().getName())) {
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

    public Room getWalkwayRoom(String model) {
        return RoomManager.getInstance().getRoomByModel(model);
    }

    /**
     * Get the map of walkways by the sub model being the key and the parent room
     * being the value.
     *
     * @return the map of walkways
     */
    public static Map<String, String> getWalkwayMap() {
        return walkwayMap;
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
