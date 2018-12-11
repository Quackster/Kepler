package org.alexdev.kepler.game.room.mapping;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.pathfinder.AffectedTile;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.models.RoomModel;
import org.alexdev.kepler.game.room.public_rooms.PoolHandler;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.rooms.items.REMOVE_FLOORITEM;
import org.alexdev.kepler.messages.outgoing.rooms.items.MOVE_FLOORITEM;
import org.alexdev.kepler.messages.outgoing.rooms.items.PLACE_FLOORITEM;
import org.alexdev.kepler.messages.outgoing.rooms.items.PLACE_WALLITEM;
import org.alexdev.kepler.messages.outgoing.rooms.items.REMOVE_WALLITEM;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RoomMapping {
    private Room room;
    private RoomModel roomModel;
    private RoomTile roomMap[][];
    private List<RoomTile> tileList;

    public RoomMapping(Room room) {
        this.room = room;
    }

    /**
     * Regenerate the entire collision map used for
     * furniture and entity detection
     */
    public void regenerateCollisionMap() {
        this.tileList = new ArrayList<>();
        this.roomModel = this.room.getModel();
        this.roomMap = new RoomTile[this.roomModel.getMapSizeX()][this.roomModel.getMapSizeY()];

        for (int x = 0; x < this.roomModel.getMapSizeX(); x++) {
            for (int y = 0; y < this.roomModel.getMapSizeY(); y++) {
                this.roomMap[x][y] = new RoomTile(this.room, new Position(x, y), this.roomModel.getTileHeight(x, y));
                this.tileList.add(this.roomMap[x][y]);
            }
        }

        this.regenerateEntityCollision();
        this.regenerateItemCollision();
    }

    /**
     * Regenerate the item collision map only.
     */
    private void regenerateItemCollision() {
        try {
            for (RoomTile tile : this.tileList) {
                tile.setHighestItem(null);
                tile.getItems().clear();
                tile.setTileHeight(tile.getDefaultHeight());
            }

            this.room.getItemManager().setSoundMachine(null);
            this.room.getItemManager().setMoodlight(null);

            List<Item> items = new ArrayList<>(this.room.getItems());
            items.sort(Comparator.comparingDouble((Item item) -> item.getPosition().getZ()));

            for (Item item : items) {
                if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
                    continue;
                }

                RoomTile tile = item.getTile();

                if (tile == null) {
                    continue;
                }

                tile.getItems().add(item);

                if (tile.getTileHeight() < item.getTotalHeight()) {
                    item.setItemBelow(tile.getHighestItem());
                    tile.setTileHeight(item.getTotalHeight());
                    tile.setHighestItem(item);

                    List<Position> affectedTiles = AffectedTile.getAffectedTiles(item);

                    for (Position position : affectedTiles) {
                        if (position.getX() == item.getPosition().getX() && position.getY() == item.getPosition().getY()) {
                            continue;
                        }

                        RoomTile affectedTile = this.getTile(position);

                        // If there's an item in the affected tiles that has a higher height, then don't override it.
                        if (affectedTile.getHighestItem() != null) {
                            if (affectedTile.getWalkingHeight() > item.getTotalHeight()) {
                                continue;
                            }
                        }

                        affectedTile.setTileHeight(item.getTotalHeight());
                        affectedTile.setHighestItem(item);
                    }

                    if (item.hasBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT)) {
                        PoolHandler.setupRedirections(this.room, item);
                    }

                    // Method to set only one jukebox per room
                    if (this.room.getItemManager().getSoundMachine() == null) {
                        if (item.hasBehaviour(ItemBehaviour.JUKEBOX) || item.hasBehaviour(ItemBehaviour.SOUND_MACHINE)) {
                            this.room.getItemManager().setSoundMachine(item);
                        }
                    }
                }
            }

            // Method to set only one moodlight per room
            for (Item item : this.room.getItemManager().getWallItems()) {
                if (item.hasBehaviour(ItemBehaviour.ROOMDIMMER)) {
                    this.room.getItemManager().setMoodlight(item);
                    break;
                }
            }

        } catch (Exception ex) {
            Log.getErrorLogger().error("Generate collision map failed for room {}", this.room.getId());
        }
    }

    /**
     * Regenerate the entity collision map only.
     */
    private void regenerateEntityCollision() {
        for (RoomTile tile : this.tileList) {
            tile.getEntities().clear();
        }

        for (Entity entity : this.room.getEntities()) {
            RoomTile tile = entity.getRoomUser().getTile();

            if (tile == null) {
                continue;
            }

            tile.addEntity(entity);
        }
    }

    /**
     * Add a specific item to the room map
     *
     * @param item the item to add
     */
    public void addItem(Item item) {
        item.setRoomId(this.room.getId());
        item.setOwnerId(this.room.getData().getOwnerId());
        item.setRollingData(null);

        this.room.getItems().add(item);

        if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            this.room.send(new PLACE_WALLITEM(item));

            if (item.hasBehaviour(ItemBehaviour.ROOMDIMMER)) {
                this.room.getItemManager().setMoodlight(item);
            }
        } else {
            this.handleItemAdjustment(item, false);
            this.regenerateCollisionMap();

            this.room.send(new PLACE_FLOORITEM(item));
        }

        if (item.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            item.setCustomData("FALSE");
        }

        item.updateEntities(null);
        ItemDao.updateItem(item);
    }

    /**
     * Move an item, will regenerate the map if the item is a floor item.
     *
     * @param item the item that is moving
     * @param isRotation whether it's just rotation or not
     *        (don't regenerate map or adjust position if it's just a rotation)
     */
    public void moveItem(Item item, boolean isRotation, Position oldPosition) {
        item.setRoomId(this.room.getId());
        item.setRollingData(null);

        if (!item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            this.handleItemAdjustment(item, isRotation);
            this.regenerateCollisionMap();

            this.room.send(new MOVE_FLOORITEM(item));
        }

        if (item.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            item.setCustomData("FALSE");
        }

        item.updateEntities(oldPosition);
        ItemDao.updateItem(item);
    }

    /**
     * Remove an item from the room.
     *
     * @param item the item that is being removed
     */
    public void removeItem(Item item) {
        this.room.getItems().remove(item);

        if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            this.room.send(new REMOVE_WALLITEM(item));
        } else {
            this.regenerateCollisionMap();
            this.room.send(new REMOVE_FLOORITEM(item));
        }

        if (item.hasBehaviour(ItemBehaviour.ROOMDIMMER)) {
            if (item.getCustomData().isEmpty()) {
                item.setCustomData(Item.DEFAULT_ROOMDIMMER_CUSTOM_DATA);
            }

            if (item.getCustomData().charAt(0) == '2') { // Roomdimmer is enabled, turn it off.
                item.setCustomData("1" + item.getCustomData().substring(1));
            }

            this.room.getItemManager().setMoodlight(null);
        }

        if (item.hasBehaviour(ItemBehaviour.DICE) || item.hasBehaviour(ItemBehaviour.WHEEL_OF_FORTUNE)) {
            item.setRequiresUpdate(false);
            item.setCustomData("");
        }

        if (item.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            item.setCustomData("FALSE");
        }

        item.updateEntities(null);

        item.getPosition().setX(0);
        item.getPosition().setY(0);
        item.getPosition().setZ(0);
        item.getPosition().setRotation(0);
        item.setRoomId(0);
        item.setRollingData(null);

        ItemDao.updateItem(item);
    }

    /**
     * Handle item adjustment.
     *
     * @param item the item
     * @param isRotation the rotation only
     */
    private void handleItemAdjustment(Item item, boolean isRotation) {
        RoomTile tile = this.getTile(item.getPosition());

        if (tile == null) {
            return;
        }

        if (!isRotation) {
            Item highestItem = tile.getHighestItem();
            item.getPosition().setZ(tile.getTileHeight());

            if (tile.getHighestItem() != null && highestItem.getRollingData() != null) {
                Item roller = highestItem.getRollingData().getRoller();

                if (highestItem.getItemBelow() != null && highestItem.getItemBelow().hasBehaviour(ItemBehaviour.ROLLER)) {
                    // If the difference between the roller, and the next item up is more than 0.5, then set the item below the floating item
                    if (Math.abs(highestItem.getPosition().getZ() - roller.getPosition().getZ()) >= 0.5) {
                        item.getPosition().setZ(roller.getPosition().getZ() + roller.getDefinition().getTopHeight());
                    }
                }

                if (!highestItem.hasBehaviour(ItemBehaviour.CAN_STACK_ON_TOP)) {
                    item.getPosition().setZ(roller.getPosition().getZ() + roller.getDefinition().getTopHeight());

                    for (Item tileItem : tile.getItems()) {
                        if (tileItem.getPosition().getZ() >= item.getPosition().getZ()) {
                            tileItem.getRollingData().setHeightUpdate(item.getDefinition().getTopHeight());
                        }
                    }
                }
            }
        }

        if (item.getPosition().getZ() > GameConfiguration.getInstance().getInteger("stack.height.limit")) {
            item.getPosition().setZ(GameConfiguration.getInstance().getInteger("stack.height.limit"));
        }
    }

    /**
     * Get the tile by {@link Position} instance
     *
     * @param position the position class to find tile
     * @return the tile, found, else null
     */
    public RoomTile getTile(Position position) {
        return getTile(position.getX(), position.getY());
    }

    /**
     * Get the tile by specified coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the tile, found, else null
     */
    public RoomTile getTile(int x, int y) {
        if (x < 0 || y < 0) {
            return null;
        }

        if (x >= this.room.getModel().getMapSizeX() || y >= this.room.getModel().getMapSizeY()) {
            return null;
        }

        if (x >= this.roomModel.getMapSizeX() || y >= this.roomModel.getMapSizeY()) {
            return null;
        }

        if (this.roomModel.getTileState(x, y) == RoomTileState.CLOSED) {
            return null;
        }

        return this.roomMap[x][y];
    }
}