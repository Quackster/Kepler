package org.alexdev.kepler.game.room.mapping;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.infobus.InfobusManager;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.interactors.InteractionType;
import org.alexdev.kepler.game.pathfinder.Pathfinder;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pets.Pet;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.handlers.walkways.WalkwaysEntrance;
import org.alexdev.kepler.game.room.handlers.walkways.WalkwaysManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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
    private CopyOnWriteArrayList<Entity> nonBlockingEntities;
    private ConcurrentHashMap<Integer, Item> items;

    private double tileHeight;
    private double defaultHeight;

    private Item highestItem;

    public RoomTile(Room room, Position position, double tileHeight) {
        this.room = room;
        this.position = position;
        this.tileHeight = tileHeight;
        this.defaultHeight = tileHeight;
        this.entities = new CopyOnWriteArrayList<>();
        this.nonBlockingEntities = new CopyOnWriteArrayList<>();
        this.items = new ConcurrentHashMap<>();
    }

    /**
     * Gets if the tile was valid.
     *
     * @param entity the entity checking
     * @param position the position of the tile
     * @return true, if successful
     */
    public static boolean isValidTile(Room room, Entity entity, Position position) {
        if (room == null ) {
            return false;
        }

        RoomTile tile = room.getMapping().getTile(position);

        if (tile == null) {
            return false;
        }

        if (entity != null) {
            if (room.getModel().getName().equals("park_a")) {
                if (!InfobusManager.getInstance().isDoorOpen()) {
                    if (position.equals(new Position(InfobusManager.getInstance().getDoorX(), InfobusManager.getInstance().getDoorY()))) {
                        return false;
                    }
                }
            }

            if (tile.getHighestItem() != null) {
                Item item = tile.getHighestItem();

                if (item.getDefinition().getSprite().equals("poolExit") && item.getPosition().equals(new Position(19, 19))) {
                    return entity.getRoomUser().containsStatus(StatusType.SWIM);
                }

                // Allow pets to walk to their own pet bed.
                if (entity.getType() == EntityType.PET) {
                    Pet pet = (Pet) entity;

                    if (pet.getDetails().getItemId() == item.getId()) {
                        return true;
                    }
                }
            }
        }

        if (tile.getEntities().size() > 0) { // Allow walk if you exist already in the tile
            if (tile.getHighestItem() != null && tile.getHighestItem().hasBehaviour(ItemBehaviour.TELEPORTER)) {
                return true;
            }

            /*if (room.isGameArena()) {
                return true;
            } else {
                return entity == null || tile.containsEntity(entity);
            }*/
            return entity == null || tile.containsEntity(entity);
        }


        if (!tile.hasWalkableFurni()) {
            if (entity != null) {
                return tile.getHighestItem() != null && tile.getHighestItem().getPosition().equals(entity.getRoomUser().getPosition());
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

            if (!tile.getHighestItem().isWalkable()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get if the highest item has walkable furni, true if no furni is on the tile.
     *
     * @return true, if successful.
     */
    public boolean hasWalkableFurni() {
        if (this.highestItem != null) {
            return this.highestItem.isWalkable();
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
        List<Position> positions = new ArrayList<>();

        for (Position POINT : Pathfinder.DIAGONAL_MOVE_POINTS) {
            Position tmp = this.position.copy().add(POINT);

            if (RoomTile.isValidTile(this.room, entity, tmp)) {
                positions.add(tmp);
            }
        }

        positions.sort(Position::getDistanceSquared);
        return (positions.size() > 0 ? positions.get(0) : null);
    }

    /**
     * Sets the entity.
     *
     * @param entity the new entity
     */
    public void addEntity(Entity entity) {
        // Don't add a user to the tile in a doorway.
        var currentPosition = new Position(this.position.getX(), this.position.getY());

        if (currentPosition.equals(this.room.getModel().getDoorLocation())) {
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

            if (this.highestItem != null && this.highestItem.getDefinition().getInteractionType() == InteractionType.WS_JOIN_QUEUE) {
                return;
            }
        }

        if (!this.room.isGameArena()) {
            if (currentPosition.equals(this.room.getModel().getDoorLocation().getSquareInFront())) {
                this.nonBlockingEntities.add(entity);
                return;
            }

            if (currentPosition.equals(this.room.getModel().getDoorLocation().getSquareInFront().getSquareLeft())) {
                this.nonBlockingEntities.add(entity);
                return;
            }

            if (currentPosition.equals(this.room.getModel().getDoorLocation().getSquareInFront().getSquareRight())) {
                this.nonBlockingEntities.add(entity);
                return;
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
        if (entities.contains(entity))
            return true;

        if (nonBlockingEntities.contains(entity))
            return true;

        return false;
    }

    /**
     * Contains the entity.
     *
     * @param entity the entity
     * @return true, if successful
     */
    public List<Entity> getOtherEntities(Entity entity) {
        List<Entity> temp = new ArrayList<>(this.entities);
        temp.removeIf(e -> e.getRoomUser().getInstanceId() == entity.getRoomUser().getInstanceId());

        return temp;
    }

    /**
     * Removes the entity.
     *
     * @param entity the entity
     */
    public void removeEntity(Entity entity) {
        this.entities.remove(entity);
        this.nonBlockingEntities.remove(entity);
    }

    public void addItem(Item item)
    {
        if (item == null)
            return;

        items.put(item.getId(), item);

        if (item.getTotalHeight() < tileHeight)
            return;

        resetHighestItem();
    }

    public void removeItem(Item item)
    {
        if (item == null)
            return;

        items.remove(item.getId());

        if (highestItem == null || item.getId() != highestItem.getId())
            return;

        resetHighestItem();
    }

    public void resetHighestItem() {
        highestItem = null;
        tileHeight = defaultHeight;

        for (var item : items.values())
        {
            if (item == null)
                continue;

            double height = item.getTotalHeight();

            if (height < tileHeight)
                continue;

            highestItem = item;
            tileHeight = height;
        }
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
     * Is the next tile lower than our current tile in walking height
     * @param otherTile the tile adjacent to this one
     * @return whether it is lower or not
     */
    public boolean isHeightDrop(RoomTile otherTile) {
        return this.getWalkingHeight() > otherTile.getWalkingHeight();
    }

    /**
     * Is the next tile higher than our current tile in walking height
     * @param otherTile the tile adjacent to this one
     * @return whether it is higher or not
     */
    public boolean isHeightUpwards(RoomTile otherTile) {
        return this.getWalkingHeight() < otherTile.getWalkingHeight();
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
     */
    public void setHighestItem(Item item) {
        highestItem = item;
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
     * Get list of non-blocking entities on this tile.
     *
     * @return the list of entities
     */
    public List<Entity> getNonBlockingEntities() {
        return this.nonBlockingEntities;
    }

    /**
     * Get all entities, including ones on non-blocking tiles, used for furni interactions
     */
    public List<Entity> getEntireEntities() {
        List<Entity> entityList = new ArrayList<>();
        entityList.addAll(this.entities);
        entityList.addAll(this.nonBlockingEntities);
        return entityList;
    }
    /**
     * Get the list of items on this tile.
     *
     * @return the list of items
     */
    public ArrayList<Item> getItems() {
        var items = new ArrayList<>(this.items.values());
        items.sort(Comparator.comparingDouble(item -> item.getPosition().getZ()));
        return items;
    }

    public double getDefaultHeight() {
        return defaultHeight;
    }

    public ArrayList<Item> getItemsAbove(Item item) {
        var items = getItems();
        items.removeIf(x -> x.getId() == item.getId() || x.getPosition().getZ() < item.getPosition().getZ());
        return items;
    }
}
