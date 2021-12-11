package org.alexdev.kepler.game.item.interactors.types;

import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.interactors.InteractionType;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.game.triggers.GenericTrigger;
import org.alexdev.kepler.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class BedInteractor extends GenericTrigger {
    @Override
    public void onEntityStop(Entity entity, RoomEntity roomEntity, Item item, boolean isRotation) {
        Position destination = entity.getRoomUser().getPosition().copy();

        if (!isValidPillowTile(item, destination)) {
            destination = convertToPillow(destination, item);
        }

        if (isValidPillowTile(item, destination)) {
            if (!RoomTile.isValidTile(roomEntity.getRoom(), roomEntity.getEntity(), destination)) {
                return;
            }

            entity.getRoomUser().warp(destination, false, false);

            roomEntity.removeDrinks();
            roomEntity.removeStatus(StatusType.DANCE);

            roomEntity.getPosition().setRotation(item.getPosition().getRotation());

            double topHeight = item.getDefinition().getTopHeight();

            if (entity.getType() == EntityType.PET) {
                topHeight = 0.5 + item.getTile().getWalkingHeight();
            }

            roomEntity.getPosition().setRotation(item.getPosition().getRotation());
            roomEntity.setStatus(StatusType.LAY, StringUtil.format(topHeight));
        }

        roomEntity.setNeedsUpdate(true);
    }

    /**
     * Converts any coordinate within the bed dimensions to the closest pillow.
     *
     * @param position to check
     * @param item the item checking against
     * @return the pillow position
     */
    public static Position convertToPillow(Position position, Item item) {
        Position destination = position.copy();

        if (!isValidPillowTile(item, position)) {
            for (Position tile : getValidPillowTiles(item)) {
                if (item.getPosition().getRotation() == 0) {
                    destination.setY(tile.getY());
                } else {
                    destination.setX(tile.getX());
                }

                break;
            }
        }

        return destination;
    }

    /**
     * Validates if the users tile is a valid pillow tile on a bed.
     *
     * @param item the bed to check for
     * @param entityPosition the entity position to check against
     * @return true, if successful
     */
    public static boolean isValidPillowTile(Item item, Position entityPosition) {
        if (entityPosition.equals(item.getPosition())) {
            return true;
        } else {
            for (Position validTile : getValidPillowTiles(item)) {
                if (validTile.equals(entityPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Gets the valid pillow tile list for a bed.
     *
     * @param item the item to check for
     * @return the list of valid coordinates
     */
    public static List<Position> getValidPillowTiles(Item item) {
        List<Position> tiles = new ArrayList<>();
        tiles.add(new Position(item.getPosition().getX(), item.getPosition().getY()));

        int validPillowX = -1;
        int validPillowY = -1;

        if (item.getPosition().getRotation() == 0) {
            validPillowX = item.getPosition().getX() + 1;
            validPillowY = item.getPosition().getY();
        }

        if (item.getPosition().getRotation() == 2) {
            validPillowX = item.getPosition().getX();
            validPillowY = item.getPosition().getY() + 1;
        }

        tiles.add(new Position(validPillowX, validPillowY));
        return tiles;
    }
}
