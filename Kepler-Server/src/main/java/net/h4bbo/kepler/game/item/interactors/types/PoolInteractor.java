package net.h4bbo.kepler.game.item.interactors.types;

import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.game.room.mapping.RoomTile;

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
            if (fromItem != null) {
                // Check if they have swimmers before trying to enter pool
                if (fromItem.getDefinition().getSprite().equals("poolEnter") ||
                    fromItem.getDefinition().getSprite().equals("poolExit")) {
                    return entity.getDetails().getPoolFigure().length() > 0;
                }
            }

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
                
                // Don't allow people to enter the booth if it's closed, or don't allow
                // if they attempt to use the pool lift without swimmers
                if (toItem.getDefinition().getSprite().equals("poolBooth") ||
                    toItem.getDefinition().getSprite().equals("poolLift")) {
                    return !toItem.getCurrentProgramValue().equals("close");
                }
            }
        }

        return true;
    }
}
