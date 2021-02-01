package org.alexdev.kepler.game.room.handlers.walkways;

import org.alexdev.kepler.game.pathfinder.Position;

import java.util.List;

public class WalkwaysEntrance {
    private int roomId;
    private int roomTargetId;
    private List<Position> fromCoords;
    private Position destination;

    public WalkwaysEntrance(int roomId, int roomTargetId, List<Position> fromCoords, Position destination) {
        this.roomId = roomId;
        this.roomTargetId = roomTargetId;
        this.fromCoords = fromCoords;
        this.destination = destination;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getRoomTargetId() {
        return roomTargetId;
    }

    public List<Position> getFromCoords() {
        return this.fromCoords;
    }

    public Position getDestination() {
        return this.destination;
    }
}
