package org.alexdev.kepler.game.item.interactors.types;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.mapping.RoomTile;

public class PoolInteractor {
    public static boolean getTileStatus(Room room, Entity entity, Position current, Position tmp, boolean isFinalMove) {
        RoomTile fromTile = room.getMapping().getTile(current);
        RoomTile toTile = room.getMapping().getTile(tmp);

        if (fromTile == null || toTile == null) {
            return false;
        }

        Item fromItem = fromTile.getHighestItem();
        Item toItem = toTile.getHighestItem();

        // Only check these below if the user is in a pool room.
        if (room.getModel().getName().startsWith("pool_") ||
                room.getModel().getName().equals("md_a")) {
            if (toItem != null) {
                // Check if they have swimmers before trying to enter pool
                if (toItem.getDefinition().getSprite().equals("poolEnter") ||
                        toItem.getDefinition().getSprite().equals("poolExit")) {
                    return entity.getDetails().getPoolFigure().length() > 0;
                }

                // Don't allow to "enter" the pool if they're already swimming
                if (entity.getRoomUser().containsStatus(StatusType.SWIM) &&
                        toItem.getDefinition().getSprite().equals("poolEnter")) {
                    return false;
                }

                // Don't allow to "leave" the pool if they're not swimming
                if (!entity.getRoomUser().containsStatus(StatusType.SWIM) &&
                        toItem.getDefinition().getSprite().equals("poolExit")) {
                    return false;
                }

                /*
                // Don't allow users to cut people in queue, force them to garound
                if (toItem.getDefinition().getSprite().contains("queue_tile2")) {
                    if (entity.getType() == EntityType.PLAYER && ((Player)entity).getNetwork().isFlashConnection()) {
                        return false;
                    }
                }
                */

                // Don't allow people to enter the booth if it's closed, or don't allow
                // if they attempt to use the pool lift without swimmers
                if (toItem.getDefinition().getSprite().equals("poolBooth") ||
                        toItem.getDefinition().getSprite().equals("poolLift")) {

                    if (toItem.getCurrentProgramValue().equals("close")) {
                        return false;
                    } else {
                        return entity.getDetails().getPoolFigure().length() > 0;
                    }
                }
            }
        }

        return true;
    }
}
