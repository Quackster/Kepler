package org.alexdev.kepler.game.room.mapping;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.pathfinder.Pathfinder;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.public_rooms.walkways.WalkwaysEntrance;
import org.alexdev.kepler.game.room.public_rooms.walkways.WalkwaysManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoomTile {
    private static List<Position> ignorePoolTiles = new ArrayList<>() {{
        add(new Position(20, 28));
        add(new Position(19, 28));
        add(new Position(17, 21));
        add(new Position(17, 20));
        add(new Position(31, 10));
        add(new Position(31, 9));
        add(new Position(19, 19));
        add(new Position(18, 19));
        add(new Position(11, 11));
        add(new Position(10, 11));
        add(new Position(21, 28));
        add(new Position(22, 28));
        add(new Position(16, 22));
        add(new Position(16, 23));
        add(new Position(30, 11));
        add(new Position(30, 12));
        add(new Position(12, 11));
        add(new Position(13, 12));
    }};
    
    private Room room;
    private Position position;
    private CopyOnWriteArrayList<Entity> entities;
    private CopyOnWriteArrayList<Item> items;

    private double tileHeight;
    private double defaultHeight;

    private Item highestItem;

    public RoomTile(Room room, Position position, double tileHeight) {
        this.room = room;
        this.position = position;
        this.tileHeight = tileHeight;
        this.defaultHeight = tileHeight;
        this.entities = new CopyOnWriteArrayList<>();
        this.items = new CopyOnWriteArrayList<>();
    }

    /**
     * Gets if the tile was valid.
     *
     * @param entity the entity checking
     * @param position the position of the tile
     * @return true, if successful
     */
    public static boolean isValidTile(Room room, Entity entity, Position position) {
        if (room == null) {
            return false;
        }

        RoomTile tile = room.getMapping().getTile(position);

        if (tile == null) {
            return false;
        }

        if (tile.getEntities().size() > 0) { // Allow walk if you exist already in the tile
            if (tile.getHighestItem() != null && tile.getHighestItem().hasBehaviour(ItemBehaviour.TELEPORTER)) {
                return true;
            }

            return entity == null || tile.containsEntity(entity);
        }


        if (!tile.hasWalkableFurni()) {
            if (entity != null) {
                return tile.getHighestItem().getPosition().equals(entity.getRoomUser().getPosition());
            }

            return false;
        }

        return true;
    }

    /**
     * Gets if the tile was valid, but if there's chairs in the way it will not be valid.
     *
     * @param entity the entity checking
     * @param position the position of the tile
     * @return true, if successful
     */
    public static boolean isValidDiagonalTile(Room room, Entity entity, Position position) {
        if (room == null) {
            return false;
        }

        RoomTile tile = room.getMapping().getTile(position);

        if (tile == null) {
            return false;
        }

        if (tile.getEntities().size() > 0) { // Allow walk if you exist already in the tile
            return entity == null || tile.containsEntity(entity);
        }

        if (tile.getHighestItem() != null) {
            if (tile.getHighestItem().hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP)) {
                return false;
            }

            if (!tile.getHighestItem().isWalkable(position)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if current tile touches target tile
     */
    public boolean touches(RoomTile targetTile) {
        return this.position.getDistanceSquared(targetTile.getPosition()) <= 2;
    }

    /**
     * Get if the highest item has walkable furni, true if no furni is on the tile.
     *
     * @return true, if successful.
     */
    public boolean hasWalkableFurni() {
        if (this.highestItem != null) {
            return this.highestItem.isWalkable(this.position);
        }

        return true;
    }

    /**
     * Get the next avaliable tile around this tile.
     *
     * @param entity the entity to check for, can be null
     * @return a valid position, else null
     */
    public Position getNextAvailablePosition(Entity entity) {
        for (Position POINT : Pathfinder.DIAGONAL_MOVE_POINTS) {
            Position tmp = this.position.copy().add(POINT);

            if (RoomTile.isValidTile(this.room, entity, tmp)) {
                return tmp;
            }
        }

        return null;
    }

    /**
     * Sets the entity.
     *
     * @param entity the new entity
     */
    public void addEntity(Entity entity) {
        // Don't add a user to the tile in a doorway.
        if (new Position(this.position.getX(), this.position.getY()).equals(this.room.getModel().getDoorLocation())) {
            return;
        }

        // If the position is a destination in a walkway, don't add a user to the tile.
        if (this.room.isPublicRoom()) {
            WalkwaysEntrance destination = WalkwaysManager.getInstance().getDestination(this.room, this.position);

            if (destination != null) {
                return;
            }

            if (this.room.getModel().getName().equals("pool_a") ||
                    this.room.getModel().getName().equals("pool_b") ||
                    this.room.getModel().getName().equals("md_a")) {

                for (Position pos : ignorePoolTiles) {
                    if (pos.equals(this.position)) {
                        return;
                    }
                }
            }
        }

        this.entities.add(entity);
    }

    /**
     * Contains the entity.
     *
     * @param entity the entity
     * @return true, if successful
     */
    public boolean containsEntity(Entity entity) {
        return this.entities.contains(entity);
    }

    /**
     * Removes the entity.
     *
     * @param entity the entity
     */
    public void removeEntity(Entity entity) {
        this.entities.remove(entity);
    }

    /**
     * Get the current position of the tile.
     *
     * @return the position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Get the current height of the tile.
     *
     * @return the tile height
     */
    public double getTileHeight() {
        return tileHeight;
    }

    /**
     * Get the current height of the tile, but take away the offset of chairs
     * and beds so users can sit on them properly.
     *
     * @return the interactive tile height
     */
    public double getWalkingHeight() {
        double height = this.tileHeight;

        if (this.highestItem != null) {
            if (this.highestItem.hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP) || this.highestItem.hasBehaviour(ItemBehaviour.CAN_LAY_ON_TOP)) {
                height -= this.highestItem.getDefinition().getTopHeight();
            }
        }

        return height;
    }

    /**
     * Set the tile height of this tile.
     *
     * @param tileHeight the new tile height
     */
    public void setTileHeight(double tileHeight) {
        this.tileHeight = tileHeight;
    }

    /**
     * Get the highest item in this tile.
     *
     * @return the highest item
     */
    public Item getHighestItem() {
        return highestItem;
    }

    /**
     * Set the highest item in this tile.
     *
     * @return the highest item
     */
    public void setHighestItem(Item highestItem) {
        this.highestItem = highestItem;
    }

    /**
     * Get list of entities on this tile.
     *
     * @return the list of entities
     */
    public List<Entity> getEntities() {
        return this.entities;
    }

    /**
     * Get the list of items on this tile.
     *
     * @return the list of items
     */
    public List<Item> getItems() {
        return items;
    }

    public double getDefaultHeight() {
        return defaultHeight;
    }
}
