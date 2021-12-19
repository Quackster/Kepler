package org.alexdev.kepler.game.room.mapping;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.interactors.InteractionType;
import org.alexdev.kepler.game.pathfinder.AffectedTile;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.models.RoomModel;
import org.alexdev.kepler.game.room.public_rooms.PoolHandler;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.rooms.items.*;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoomMapping {
    public static final String TELEPORTER_CLOSE = "0";
    public static final String TELEPORTER_OPEN = "1";

    public static int FORTUNE_OFF = 8;
    public static int FORTUNE_NO_STATE = -1;

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

        this.tileList = new CopyOnWriteArrayList<>();
        this.roomModel = this.room.getModel();
        this.roomMap = new RoomTile[this.roomModel.getMapSizeX()][this.roomModel.getMapSizeY()];

        for (int x = 0; x < this.roomModel.getMapSizeX(); x++) {
            for (int y = 0; y < this.roomModel.getMapSizeY(); y++) {
                var roomTile = new RoomTile(this.room, new Position(x, y), this.roomModel.getTileHeight(x, y));
                this.roomMap[x][y] = roomTile;
                this.tileList.add(roomTile);
            }
        }

        try {
            this.room.getItemManager().setSoundMachine(null);
            this.room.getItemManager().setMoodlight(null);

            if (!this.room.isGameArena()) {
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

                    for (Position position : AffectedTile.getAffectedTiles(item)) {
                        RoomTile affectedTile = this.getTile(position);

                        if (affectedTile == null) {
                            continue;
                        }

                        affectedTile.addItem(item);

                        if (item.hasBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT)) {
                            PoolHandler.setupRedirections(this.room, item);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            Log.getErrorLogger().error(String.format("Generate collision map failed for room %s", this.room.getId()), ex);
        }

        try {
            for (Entity entity : this.room.getEntities()) {
                RoomTile tile = entity.getRoomUser().getTile();

                if (tile == null) {
                    continue;
                }

                tile.addEntity(entity);
            }

        } catch (Exception ex) {
            Log.getErrorLogger().error(String.format("Generate entity map failed for room %s", this.room.getId()), ex);
        }

        refreshRoomItems();
    }

    public void refreshRoomItems() {
        this.room.getItemManager().setSoundMachine(null);
        this.room.getItemManager().setMoodlight(null);

        for (Item item : room.getItemManager().getFloorItems()) {
            if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
                continue;
            }

            RoomTile tile = item.getTile();

            if (tile == null) {
                continue;
            }

            // Method to set only one jukebox per room
            if (this.room.getItemManager().getSoundMachine() == null) {
                if (item.hasBehaviour(ItemBehaviour.JUKEBOX) || item.hasBehaviour(ItemBehaviour.SOUND_MACHINE)) {
                    this.room.getItemManager().setSoundMachine(item);
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
    }

    /**
     * Add a specific item to the room map
     *
     * @param item the item to add
     */
    public void addItem(Player player, Item item) {
        item.setRoomId(this.room.getId());
        item.setOwnerId(this.room.getData().getOwnerId());
        item.setRollingData(null);

        if (item.hasBehaviour(ItemBehaviour.DICE)) {
            // For some reason the client expects the HC dice to have a default of 1 while the normal dice a default of 0 (off)

            // Client expects default of 1 for HC dices
            if (item.getDefinition().getSprite().equals("hcdice")) {
                item.setCustomData("1");
            } else if (item.getDefinition().getSprite().equals("edice")) {
                // Client expects default of 0 (off) for 'normal'/'oldskool' dices
                item.setCustomData("0");
            } else {
                // Handle custom furniture dices (TODO: define behaviour differences between HC dice and 'oldskool' dices)
                item.setCustomData("1");
            }
        }

        this.room.getItems().add(item);

        if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            this.room.send(new PLACE_WALLITEM(item));

            if (item.hasBehaviour(ItemBehaviour.ROOMDIMMER)) {
                this.room.getItemManager().setMoodlight(item);
            }
        } else {
            this.handleItemAdjustment(item, false);

            for (Position position : AffectedTile.getAffectedTiles(item)) {
                RoomTile affectedTile = this.getTile(position);

                if (affectedTile == null) {
                    continue;
                }

                affectedTile.addItem(item);
            }

            this.room.send(new PLACE_FLOORITEM(item));
        }

        if (item.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            item.setCustomData("FALSE");
        }

        item.updateEntities(null);
        item.save();

        if (!item.getDefinition().hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            item.getDefinition().getInteractionType().getTrigger().onItemPlaced(player, this.room, item);
        }

        refreshRoomItems();
    }

    /**
     * Move an item, will regenerate the map if the item is a floor item.
     *
     * @param item the item that is moving
     */
    public void moveItem(Player player, Item item, Position newPosition, Position oldPosition) {
        boolean isRotation = false;

        if (item.getPosition().equals(new Position(newPosition.getX(), newPosition.getY())) && item.getPosition().getRotation() != newPosition.getRotation()) {
            isRotation = true;
        }

        for (Position position : AffectedTile.getAffectedTiles(item)) {
            RoomTile affectedTile = this.getTile(position);

            if (affectedTile == null) {
                continue;
            }

            affectedTile.removeItem(item);
        }

        Item itemBelow = item.getItemBelow();
        Item itemAbove = item.getItemAbove();

        item.getPosition().setX(newPosition.getX());
        item.getPosition().setY(newPosition.getY());
        item.getPosition().setRotation(newPosition.getRotation());
        item.setRoomId(this.room.getId());
        item.setRollingData(null);
        resetExtraData(item, false);

        if (!item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            this.handleItemAdjustment(item, isRotation);

            for (Position position : AffectedTile.getAffectedTiles(item)) {
                RoomTile affectedTile = this.getTile(position);

                if (affectedTile == null) {
                    continue;
                }

                affectedTile.addItem(item);
            }

            this.room.send(new MOVE_FLOORITEM(item));
        }

        if (item.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            item.setCustomData("FALSE");
        }

        item.updateEntities(oldPosition);
        item.save();

        item.getDefinition().getInteractionType().getTrigger().onItemMoved(player, room, item, isRotation, oldPosition, itemBelow, itemAbove);
        refreshRoomItems();
    }

    /**
     * Remove an item from the room.
     *
     * @param item the item that is being removed
     */
    public void removeItem(Player player, Item item) {
        item.getDefinition().getInteractionType().getTrigger().onItemPickup(player, this.room, item);
        this.room.getItems().remove(item);

        if (item.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            this.room.send(new REMOVE_WALLITEM(item));
        } else {
            for (Position position : AffectedTile.getAffectedTiles(item)) {
                RoomTile affectedTile = this.getTile(position);

                if (affectedTile == null) {
                    continue;
                }

                affectedTile.removeItem(item);
            }

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

        resetExtraData(item, false);
        item.updateEntities(null);

        item.getPosition().setX(0);
        item.getPosition().setY(0);
        item.getPosition().setZ(0);
        item.getPosition().setRotation(0);
        item.setRoomId(0);
        item.setRollingData(null);
        item.save();

        refreshRoomItems();
    }

    public static boolean resetExtraData(Item item, boolean roomLoad) {
        if (item.hasBehaviour(ItemBehaviour.DICE)) {
            item.setRequiresUpdate(false);

            // For some reason the client expects the HC dice to have a default of 1 while the normal dice a default of 0 (off)
            // Client expects default of 1 for HC dices
            switch (item.getDefinition().getSprite()) {
                case "edicehc":
                    if (roomLoad) {
                        if (!item.getCustomData().equals("0")) {
                            item.setCustomData("0");
                            return true;
                        }
                    } else {
                        if (!item.getCustomData().equals("1")) {
                            item.setCustomData("1");
                            return true;
                        }
                    }
                    break;
                case "edice":
                    // Client expects default of 0 (off) for 'normal'/'oldskool' dices
                    if (!item.getCustomData().equals("0")) {
                        item.setCustomData("0");
                        return true;
                    }
                    break;
                default:
                    // Handle custom furniture dices (TODO: define behaviour differences between HC dice and 'oldskool' dices)
                    if (!item.getCustomData().equals("1")) {
                        item.setCustomData("1");
                        return true;
                    }
                    break;
            }
        }


        if (item.getDefinition().getInteractionType() == InteractionType.LERT) {
            if (!item.getCustomData().equals("0")) {
                item.setCustomData("0");
                return true;
            }
        }

        if (item.getDefinition().getInteractionType() == InteractionType.FORTUNE) {
            if (!item.getCustomData().equals(String.valueOf(FORTUNE_OFF))) {
                item.setCustomData(String.valueOf(FORTUNE_OFF));
                return true;
            }

            item.setRequiresUpdate(false);
        }

        if (item.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            if (!item.getCustomData().equals(TELEPORTER_CLOSE)) {
                item.setCustomData(TELEPORTER_CLOSE);
                return true;
            }

        }

        if (item.isCurrentRollBlocked()) {
            item.setCurrentRollBlocked(false);
        }

        return false;
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
            double tileHeight = tile.getTileHeight();

            if (highestItem != null && highestItem.getId() == item.getId()) {
                tileHeight -= highestItem.getTotalHeight();

                double defaultHeight = this.room.getModel().getTileHeight(item.getPosition().getX(), item.getPosition().getY());

                if (tileHeight < defaultHeight) {
                    tileHeight = defaultHeight;
                }
            }

            item.getPosition().setZ(tileHeight);

            if (highestItem != null && highestItem.getRollingData() != null) {
                Item roller = highestItem.getRollingData().getRoller();

                if (highestItem.getItemBelow() != null && highestItem.getItemBelow().hasBehaviour(ItemBehaviour.ROLLER)) {
                    // If the difference between the roller, and the next item up is more than 0.5, then set the item below the floating item
                    if (Math.abs(highestItem.getPosition().getZ() - roller.getPosition().getZ()) >= 0.5) {
                        item.getPosition().setZ(roller.getPosition().getZ() + roller.getDefinition().getTopHeight());
                    }
                }

                item.getPosition().setZ(roller.getPosition().getZ() + roller.getDefinition().getTopHeight());

                /*if (!highestItem.hasBehaviour(ItemBehaviour.CAN_STACK_ON_TOP)) {
                    item.getPosition().setZ(roller.getPosition().getZ() + roller.getDefinition().getTopHeight());

                    /*for (Item tileItem : tile.getItems()) {
                        if (tileItem.getPosition().getZ() >= item.getPosition().getZ()) {
                            tileItem.getRollingData().setHeightUpdate(item.getDefinition().getTopHeight());
                        }
                    }
                }*/
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

    public Position getRandomWalkableBound(Entity entity) {
        Position position = null;

        boolean isWalkable = false;
        int attempts = 0;
        int maxAttempts = 10;

        while (attempts < maxAttempts) {
            attempts++;

            int randomX = this.room.getModel().getRandomBound(0);
            int randomY = this.room.getModel().getRandomBound(1);
            position = new Position(randomX, randomY);

            if (RoomTile.isValidTile(this.room, entity, position)) {
                return position;
            }
        }

        return null;
    }
}