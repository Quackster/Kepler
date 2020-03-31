package org.alexdev.kepler.game.item;

import org.alexdev.kepler.dao.mysql.TeleporterDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.item.roller.RollingData;
import org.alexdev.kepler.game.pathfinder.AffectedTile;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.game.room.mapping.RoomTileState;
import org.alexdev.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;
import org.alexdev.kepler.messages.outgoing.rooms.items.STUFFDATAUPDATE;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.StringUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Item {
    public static final String DEFAULT_ROOMDIMMER_CUSTOM_DATA = "1,1,1,#000000,255";
    public static final String PRESENT_DELIMETER = "|";

    private int id;
    private int orderId;
    private int ownerId;
    private int roomId;
    private int teleporterId;

    private ItemDefinition definition;
    private int definitionId;

    private Item itemBelow;
    private Item itemAbove;

    private Position position;
    private String wallPosition;
    private String customData;
    private String currentProgram;
    private String currentProgramValue;

    private boolean requiresUpdate;
    private boolean isCurrentRollBlocked;
    private RollingData rollingData;
    private boolean isHidden;

    public Item() {
        this.id = 0;
        this.definition = new ItemDefinition();
        this.position = new Position();
        this.customData = "";
        this.wallPosition = "";
        this.currentProgram = "";
        this.currentProgramValue = "";
        this.requiresUpdate = false;
        this.rollingData = null;
    }

    public void fill(int id, int orderId, int ownerId, int roomId, int definitionId, int X, int Y, double Z, int rotation, String wallPosition, String customData, boolean isHidden) {
        this.id = id;
        this.orderId = orderId;
        this.ownerId = ownerId;
        this.roomId = roomId;
        this.definition = null;
        this.definitionId = definitionId;
        this.position = new Position(X, Y, Z, rotation, rotation);
        this.wallPosition = wallPosition;
        this.customData = customData;
        this.rollingData = null;
        this.isHidden = isHidden;
        this.setDefinitionId(this.definitionId);

        if (this.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            this.teleporterId = TeleporterDao.getTeleporterId(this.id);
        }
    }

    /**
     * Broadcast item program to current room, used for the pool lift, booth, pool ladders, etc
     * for special effects like splashing, closing/open curtains, etc.
     *
     * @param value the new program value to show
     */
    public void showProgram(String value) {
        if (value != null) {
            this.currentProgramValue = value;
        }

        Room room = this.getRoom();

        if (room != null) {
            room.send(new SHOWPROGRAM(new String[]{this.currentProgram, this.currentProgramValue}));
        }
    }

    /**
     * Update user statuses on items with their old position and new position.
     * The old position is never null if the item is moved.
     *
     * @param oldPosition the old position of the item
     */
    public void updateEntities(Position oldPosition) {
        if (this.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
            return;
        }

        List<Entity> entitiesToUpdate = new ArrayList<>();

        if (oldPosition != null) {
            for (Position position : AffectedTile.getAffectedTiles(this, oldPosition.getX(), oldPosition.getY(), oldPosition.getRotation())) {
                RoomTile tile = this.getRoom().getMapping().getTile(position);

                if (tile == null) {
                    continue;
                }

                entitiesToUpdate.addAll(tile.getEntities());
            }
        }

        for (Position position : AffectedTile.getAffectedTiles(this)) {
            RoomTile tile = this.getRoom().getMapping().getTile(position);

            if (tile == null) {
                continue;
            }

            entitiesToUpdate.addAll(tile.getEntities());
        }

        // Reset people teleporting
        if (this.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            for (Entity entity : this.getRoom().getEntities()) {
                if (entity.getType() == EntityType.PLAYER) {

                    Player player = (Player) entity;

                    if (entity.getRoomUser().getPosition().equals(this.position) || player.getRoomUser().getAuthenticateTelporterId() == id) {
                        player.getRoomUser().setAuthenticateTelporterId(-1);
                        player.getRoomUser().setWalkingAllowed(true);
                    }
                }
            }
        }

        for (Entity entity : entitiesToUpdate) {
            entity.getRoomUser().invokeItem(oldPosition);
        }
    }

    /**
     * Get the total height, which is the height of the item plus stack size.
     *
     * @return the total height
     */
    public double getTotalHeight() {
        return this.position.getZ() + this.getDefinition().getTopHeight();
    }

    /**
     * Get whether or not the item is walkable.
     *
     * @return true, if successful.
     */
    public boolean isWalkable(Position selectedPosition) {
        if (this.getDefinition().getSprite().equals("poolLift")) {
            return this.currentProgramValue.equals("open");
        }

        if (this.getDefinition().getSprite().equals("poolBooth")) {
            return this.currentProgramValue.equals("open");
        }

        if (this.hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP)) {
            return true;
        }

        if (this.hasBehaviour(ItemBehaviour.CAN_LAY_ON_TOP)) {
            /*Position newPosition = null;

            for (Position tile : BedInteractor.getValidPillowTiles(this)) {
                newPosition = selectedPosition.copy();

                if (this.position.getRotation() == 0) {
                    newPosition.setY(tile.getY());
                } else {
                    newPosition.setX(tile.getX());
                }

                break;

            }

            return newPosition != null && getRoom().getMapping().getTile(newPosition).getEntities().isEmpty();*/
            return true;
        }

        if (this.hasBehaviour(ItemBehaviour.CAN_STAND_ON_TOP)) {
            return true;
        }

        if (this.hasBehaviour(ItemBehaviour.TELEPORTER)) {
            return this.customData.equals("TRUE");
        }

        if (this.hasBehaviour(ItemBehaviour.GATE)) {
            return this.isGateOpen();
        }
        
        return false;
    }

    public boolean isGateOpen() {
        if (this.hasBehaviour(ItemBehaviour.GATE)) {
            return this.customData.equals("O");
        }

        return false;
    }

    /**
     * Send status update of the item.
     */
    public void updateStatus() {
        Room room = this.getRoom();

        if (room != null) {
            room.send(new STUFFDATAUPDATE(this));
        }
    }


    /**
     * Queue item saving.
     */
    public void save() {
        GameScheduler.getInstance().queueSaveItem(this);
    }

    /**
     * Queue item deletion.
     */
    public void delete() {
        GameScheduler.getInstance().queueDeleteItem(this.getId());
    }

    /**
     * Serialise item function for item handling packets.
     *
     * @param response the response to serialise to
     */
    public void serialise(NettyResponse response) {
        ItemDefinition definition = this.getDefinition();

        if (definition.hasBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT)) {
            response.writeDelimeter(this.customData, ' ');
            response.writeString(definition.getSprite());
            response.writeDelimeter(this.position.getX(), ' ');
            response.writeDelimeter(this.position.getY(), ' ');
            response.writeDelimeter((int) this.position.getZ(), ' ');
            response.write(this.position.getRotation());

            if (this.hasBehaviour(ItemBehaviour.EXTRA_PARAMETER)) {
                response.write(" 2");
            }

            response.write(Character.toString((char) 13));
        } else {
            if (this.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
                response.writeDelimeter(this.id, (char) 9);
                response.writeDelimeter(definition.getSprite(), (char) 9);
                response.writeDelimeter(" ", (char) 9);
                response.writeDelimeter(this.wallPosition, (char) 9);

                if (this.customData.length() > 0) {
                    if (this.hasBehaviour(ItemBehaviour.POST_IT)) {
                        response.write(this.customData.substring(0, 6)); // Only show post-it colour
                    } else {
                        response.write(this.customData);
                    }
                }

                response.write(Character.toString((char) 13));
            } else {
                response.writeString(this.id);
                response.writeString(definition.getSprite());
                response.writeInt(this.position.getX());
                response.writeInt(this.position.getY());
                response.writeInt(definition.getLength());
                response.writeInt(definition.getWidth());
                response.writeInt(this.position.getRotation());
                response.writeString(StringUtil.format(this.position.getZ()));
                response.writeString(definition.getColour());
                response.writeString("");
                response.writeInt(this.hasBehaviour(ItemBehaviour.ROLLER) ? 2 : 0); // Required 2 for rollers to enable animation when rollers are used!

                if (this.hasBehaviour(ItemBehaviour.PRESENT)) {
                    String[] presentData = this.customData.split(Pattern.quote(Item.PRESENT_DELIMETER));
                    if (presentData.length >= 3) {
                        response.writeString("!" + presentData[2]);
                    } else {
                        response.writeString("");
                    }
                } else {
                    response.writeString(this.customData);
                }

            }
        }
    }

    /**
     * Check if the move is valid before moving an item. Will prevent long
     * furniture from being on top of rollers, will prevent placing rollers on top of other rollers.
     * Will prevent items being placed on closed tile states.
     *
     * @param room the room to check inside
     * @param x the new x to check
     * @param y the new y to check
     * @param rotation the new rotation to check
     * @return true, if successful
     */
    public boolean isValidMove(Item item, Room room, int x, int y, int rotation) {
        RoomTile tile = room.getMapping().getTile(x, y);

        if (tile == null) {
            return false;
        }

        boolean isRotation = (item.getPosition().getRotation() != rotation) && (new Position(x, y).equals(item.getPosition())
                || (item.getRollingData() != null && new Position(x, y).equals(item.getRollingData().getNextPosition()))
                || (item.getRollingData() != null && new Position(x, y).equals(item.getRollingData().getFromPosition())));

        if (isRotation) {
            if (item.getRollingData() != null) {
                return false; // Don't allow rotating items when they're rolling
            }

            if (item.getDefinition().getLength() <= 1 && item.getDefinition().getWidth() <= 1) {
                return true;
            }
        }

        for (Position position : AffectedTile.getAffectedTiles(this, x, y, rotation)) {
            tile = room.getMapping().getTile(position);

            if (tile == null) {
                return false;
            }

            if (room.getModel().getTileState(position.getX(), position.getY()) == RoomTileState.CLOSED) {
                return false;
            }

            if ((tile.getWalkingHeight() + item.getDefinition().getTopHeight()) > GameConfiguration.getInstance().getInteger("stack.height.limit")) {
                return false;
            }

            if (tile.getEntities().size() > 0) {
                if (!item.isWalkable(new Position(x, y))) {
                    return false;
                }
            }

            Item highestItem = tile.getHighestItem();

            if (highestItem != null && highestItem.getId() != item.getId()) {
                if (!this.canPlaceOnTop(item, highestItem)) {
                    return false;
                }
            }

            for (Item tileItem : tile.getItems()) {
                if (tileItem.getId() == item.getId()) {
                    continue;
                }

                if (!this.canPlaceOnTop(item, tileItem)) {
                    return false;
                }

                if (tileItem.hasBehaviour(ItemBehaviour.ROLLER)) {
                    if (this.hasBehaviour(ItemBehaviour.ROLLER)) {
                        return false; // Can't place rollers on top of rollers
                    }

                    if ((this.getDefinition().getLength() > 1 || this.getDefinition().getWidth() > 1) && (this.hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP) || this.hasBehaviour(ItemBehaviour.CAN_LAY_ON_TOP))) {
                        return false; // Chair or bed is too big to place on rollers.
                    }
                }
            }
        }


        return true;
    }

    /**
     * Get if placing an item on top of another item is allowed.
     * @param item the item to place
     * @param tileItem the item to check if they're allowed to place on top of
     * @return true, if successful
     */
    private boolean canPlaceOnTop(Item item, Item tileItem) {
        // Don't allow putting rollers on top of stackable objects
        if (item.hasBehaviour(ItemBehaviour.ROLLER) && tileItem.hasBehaviour(ItemBehaviour.CAN_STACK_ON_TOP) && !tileItem.hasBehaviour(ItemBehaviour.PLACE_ROLLER_ON_TOP)) {
            if (tileItem.getDefinition().getTopHeight() >= 0.1) {
                return false;
            }
        }

        // If the item is rolling, we can place on the square
        if (tileItem.rollingData != null) {
            return true;
        }

        // Can't place items on solid objects
        if (tileItem.hasBehaviour(ItemBehaviour.SOLID) && !tileItem.hasBehaviour(ItemBehaviour.CAN_STACK_ON_TOP)) {
            return false;
        }

        // Can't place gates on solid rollers
        if (tileItem.hasBehaviour(ItemBehaviour.ROLLER) && item.hasBehaviour(ItemBehaviour.GATE)) {
            return false;
        }

        // Can't place items on sittable items
        if (tileItem.hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP)) {
            return false;
        }

        // Can't place item on layable items
        if (tileItem.hasBehaviour(ItemBehaviour.CAN_LAY_ON_TOP)) {
            return false;
        }

        return true;
    }

    /**
     * Get the room tile this item is on.
     *
     * @return the room tile, else null
     */
    public RoomTile getTile() {
        Room room = this.getRoom();

        if (room != null) {
            return this.getRoom().getMapping().getTile(this.position);
        }

        return null;
    }

    /**
     * Get if the item has a type of behaviour.
     *
     * @param behaviour the behaviour to check
     * @return true, if successful
     */
    public boolean hasBehaviour(ItemBehaviour behaviour) {
        return this.getDefinition().hasBehaviour(behaviour);
    }

    public ItemDefinition getDefinition() {
        if (this.definition != null) { // Used for public room items
            return this.definition;
        }

        // Always use ItemManager to retrieve private flat definitions
        return ItemManager.getInstance().getDefinition(this.definitionId);
    }

    public void setDefinitionId(int definitionId) {
        this.definition = null;
        this.definitionId = definitionId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getTeleporterId() {
        return teleporterId;
    }

    public void setTeleporterId(int teleporterId) {
        this.teleporterId = teleporterId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getWallPosition() {
        return wallPosition;
    }

    public void setWallPosition(String wallPosition) {
        this.wallPosition = wallPosition;
    }

    public String getCurrentProgram() {
        return currentProgram;
    }

    public void setCurrentProgram(String currentProgram) {
        this.currentProgram = currentProgram;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public String getCurrentProgramValue() {
        return currentProgramValue;
    }

    public Room getRoom() {
        return RoomManager.getInstance().getRoomById(this.roomId);
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Item getItemBelow() {
        return itemBelow;
    }

    public Item getItemAbove() {
        int position = this.getTile().getItems().indexOf(this);

        if (position > -1) {
            int nextPosition = position + 1;

            if (nextPosition >= this.getTile().getItems().size()) {
                return null;
            }

            return this.getTile().getItems().get(nextPosition);

        }

        return null;
    }

    public void setItemBelow(Item itemBelow) {
        this.itemBelow = itemBelow;
    }

    public boolean getRequiresUpdate() {
        return requiresUpdate;
    }

    public void setRequiresUpdate(boolean requiresUpdate) {
        this.requiresUpdate = requiresUpdate;
    }

    public RollingData getRollingData() {
        return rollingData;
    }

    public void setRollingData(RollingData rollingData) {
        this.rollingData = rollingData;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isCurrentRollBlocked() {
        return isCurrentRollBlocked;
    }

    public void setCurrentRollBlocked(boolean currentRollBlocked) {
        isCurrentRollBlocked = currentRollBlocked;
    }
}

