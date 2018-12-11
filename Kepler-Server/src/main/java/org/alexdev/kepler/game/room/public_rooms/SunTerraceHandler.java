package org.alexdev.kepler.game.room.public_rooms;

import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;

public class SunTerraceHandler {
    public static boolean isRedirected(RoomEntity roomEntity, int targetX, int targetY) {
        Room room = roomEntity.getRoom();

        if (!room.getModel().getName().equals("sun_terrace")) {
            return false;
        }

        double currentZ = roomEntity.getPosition().getZ();
        double goalZ  = room.getMapping().getTile(targetX, targetY).getTileHeight();

        if (!(currentZ >= 8) && goalZ >= 8 && roomEntity.getPosition().getX() != 4 && roomEntity.getPosition().getY() != 18) {
            return true;
        }

        return targetX == 4 && targetY == 18 && roomEntity.getPosition().getX() != 6 && roomEntity.getPosition().getY() != 21;
    }
}
