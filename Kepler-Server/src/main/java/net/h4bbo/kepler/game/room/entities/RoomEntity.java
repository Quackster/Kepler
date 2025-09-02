package net.h4bbo.kepler.game.room.entities;

import net.h4bbo.kepler.game.GameScheduler;
import net.h4bbo.kepler.game.bot.BotManager;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.item.interactors.types.BedInteractor;
import net.h4bbo.kepler.game.item.roller.RollingData;
import net.h4bbo.kepler.game.moderation.ChatManager;
import net.h4bbo.kepler.game.pathfinder.Pathfinder;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.pathfinder.Rotation;
import net.h4bbo.kepler.game.pets.PetManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.game.room.RoomUserStatus;
import net.h4bbo.kepler.game.room.enums.DrinkType;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.game.room.handlers.walkways.WalkwaysEntrance;
import net.h4bbo.kepler.game.room.handlers.walkways.WalkwaysManager;
import net.h4bbo.kepler.game.room.managers.RoomTimerManager;
import net.h4bbo.kepler.game.room.mapping.RoomTile;
import net.h4bbo.kepler.game.room.public_rooms.SunTerraceHandler;
import net.h4bbo.kepler.game.room.tasks.WaveTask;
import net.h4bbo.kepler.game.texts.TextsManager;
import net.h4bbo.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import net.h4bbo.kepler.messages.outgoing.rooms.user.USER_OBJECTS;
import net.h4bbo.kepler.messages.outgoing.rooms.user.USER_STATUSES;
import net.h4bbo.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class RoomEntity {
    private Entity entity;
    private Position position;
    private Position goal;
    private Position nextPosition;
    private Room room;

    private RollingData rollingData;
    private RoomTimerManager timerManager;

    private Map<String, RoomUserStatus> statuses;
    private LinkedList<Position> path;

    private int instanceId;
    private Item lastItemInteraction;

    private boolean isWalking;
    private boolean isWalkingAllowed;
    private boolean beingKicked;
    private boolean needsUpdate;
    private boolean enableWalkingOnStop;

    public RoomEntity(Entity entity) {
        this.entity = entity;
        this.statuses = new ConcurrentHashMap<>();
        this.path = new LinkedList<>();
        this.timerManager = new RoomTimerManager(this);
    }

    public void reset() {
        this.statuses.clear();
        this.path.clear();
        this.nextPosition = null;
        this.goal = null;
        this.room = null;
        this.rollingData = null;
        this.lastItemInteraction = null;
        this.isWalking = false;
        this.isWalkingAllowed = true;
        this.beingKicked = false;
        this.instanceId = -1;
        this.timerManager.resetRoomTimer();
    }

    /**
     * Kick a user from the room.
     *
     * @param allowWalking whether the user can interrupt themselves walking towards the door
     */
    public void kick(boolean allowWalking) {
        Position doorLocation = this.getRoom().getModel().getDoorLocation();

        if (doorLocation == null) {
            this.getRoom().getEntityManager().leaveRoom(this.entity, true);
            return;
        }

        // If we're standing in the door, immediately leave room
        if (this.getPosition().equals(doorLocation)) {
            this.getRoom().getEntityManager().leaveRoom(this.entity, true);
            return;
        }

        // Attempt to walk to the door
        this.walkTo(doorLocation.getX(), doorLocation.getY());
        this.isWalkingAllowed = allowWalking;
        this.beingKicked = true;

        // If user isn't walking, leave immediately
        if (!this.isWalking) {
            this.getRoom().getEntityManager().leaveRoom(this.entity, true);
        }
    }

    /**
     * Make a user walk to specific coordinates. The goal must be valid and reachable.
     *
     * @param X the X coordinates
     * @param Y the Y coordinate
     */
    public boolean walkTo(int X, int Y) {
        if (this.room == null) {
            return false;
        }

        if (SunTerraceHandler.isRedirected(this, X, Y)) {
            return false;
        }

        if (this.nextPosition != null) {
            Position oldPosition = this.position.copy();

            this.position.setX(this.nextPosition.getX());
            this.position.setY(this.nextPosition.getY());
            this.updateNewHeight(this.position);

            if (this.getCurrentItem() != null) {
                if (this.getCurrentItem().getDefinition().getInteractionType().getTrigger() != null) {
                    this.getCurrentItem().getDefinition().getInteractionType().getTrigger().onEntityStep(entity, this, this.getCurrentItem(), oldPosition);
                }
            }

        }

        RoomTile tile = this.room.getMapping().getTile(X, Y);

        if (tile == null) {
            //System.out.println("User requested " + X + ", " + Y + " from " + this.position);
            return false;
        }

        this.goal = new Position(X, Y);
        //System.out.println("User requested " + this.goal + " from " + this.position + " with item " + (tile.getHighestItem() != null ? tile.getHighestItem().getDefinition().getSprite() : "NULL"));

        if (!RoomTile.isValidTile(this.room, this.entity, this.goal)) {
            return false;
        }

        if (tile.getHighestItem() != null && tile.getHighestItem().hasBehaviour(ItemBehaviour.CAN_LAY_ON_TOP)) {
            if (!BedInteractor.isValidPillowTile(tile.getHighestItem(), this.goal)) {
                Position destination = BedInteractor.convertToPillow(this.goal, tile.getHighestItem());
                this.walkTo(destination.getX(), destination.getY());
                return true;
            }
        }

        //AStar aStar = new AStar(this.room.getModel());
        //var pathList = aStar.calculateAStarNoTerrain(this.entity, this.position, this.goal);

        LinkedList<Position> pathList = Pathfinder.makePath(this.entity, this.position.copy(), this.goal.copy());

        if (pathList == null) {
            return false;
        }

        if (pathList.size() > 0) {
            this.path = pathList;
            this.isWalking = true;
            return true;
        }

        return false;
    }

    /**
     * Called to make a user stop walking.
     */
    public void stopWalking() {
        this.path.clear();
        this.isWalking = false;
        this.needsUpdate = true;
        this.nextPosition = null;
        this.removeStatus(StatusType.MOVE);

        if (this.enableWalkingOnStop) {
            this.enableWalkingOnStop = false;
            this.isWalkingAllowed = true;
        }

        if (this.entity.getType() == EntityType.PLAYER) {
            if (!this.beingKicked) {
                WalkwaysEntrance entrance = WalkwaysManager.getInstance().getWalkway(this.getRoom(), this.getPosition());

                if (entrance != null) {
                    Room room = RoomManager.getInstance().getRoomById(entrance.getRoomTargetId());

                    if (room != null) {
                        room.getEntityManager().enterRoom(this.entity, entrance.getDestination());
                        return;
                    }
                }
            }

            boolean leaveRoom = this.beingKicked;
            Position doorPosition = this.getRoom().getModel().getDoorLocation();

            if (doorPosition.equals(this.getPosition())) {
                leaveRoom = true;
            }

            if (this.getRoom().isPublicRoom()) {
                if (WalkwaysManager.getInstance().getWalkway(this.getRoom(), doorPosition) != null) {
                    leaveRoom = false;
                }
            }

            // Leave room if the tile is the door and we are in a flat or we're being kicked
            if (leaveRoom || this.beingKicked) {
                this.getRoom().getEntityManager().leaveRoom(this.entity, true);
                return;
            }
        }

        this.invokeItem(null, false);
    }
    /**
     * Triggers the current item that the player has walked on top of.
     */
    public void invokeItem(Position oldPosition, boolean instantUpdate) {
        this.position.setZ(this.getTile().getWalkingHeight());

        Item item = /*isRolling ? this.room.getMapping().getTile(this.rollingData.getNextPosition()).getHighestItem() : */this.getCurrentItem();

        if (item == null || (!item.hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP) || !item.hasBehaviour(ItemBehaviour.CAN_LAY_ON_TOP))) {
            if (this.containsStatus(StatusType.SIT) || this.containsStatus(StatusType.LAY)) {
                this.removeStatus(StatusType.SIT);
                this.removeStatus(StatusType.LAY);
            }

            if (item == null) {
                if (this.lastItemInteraction != null) {
                    var trigger = this.lastItemInteraction.getDefinition().getInteractionType().getTrigger();

                    if (trigger != null) {
                        trigger.onEntityLeave(this.entity, this, this.lastItemInteraction);
                    }

                    this.lastItemInteraction = null;
                }
            }
        }

        if (item != null) {
            var trigger = item.getDefinition().getInteractionType().getTrigger();

            if (trigger != null) {
                trigger.onEntityStop(this.entity, this, item, (oldPosition != null && oldPosition.equals(this.position)));
                this.lastItemInteraction = item;


/*                final AtomicReference<Double> x = new AtomicReference<Double>(0.5);
                this.room.getTaskManager().scheduleTask("taskName", ()->{
                    try {
                        this.setStatus(StatusType.LAY, StringUtil.format(x.getAndSet(x.get() + 0.1)));
                        this.setNeedsUpdate(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }, 5, 1, TimeUnit.SECONDS);*/
            }
        }

        this.updateNewHeight(this.position);

        if (instantUpdate) {
            this.room.send(new USER_STATUSES(List.of(this.entity)));
        } else {
            this.needsUpdate = true;
        }
    }

    /**
     * Assign a hand item to an entity, either by carry ID or carry name.
     *
     * @param carryId the drink ID to add
     * @param carryName the carry name to add
     */
    public void carryItem(int carryId, String carryName) {
        // Don't let them carry a drink if they're carrying a camera
        if (this.containsStatus(StatusType.CARRY_ITEM)) {
            return;
        }


        StatusType carryStatus = null;
        StatusType useStatus = null;

        boolean useRawString = false;

        DrinkType[] drinks = new DrinkType[26];
        drinks[1] = DrinkType.DRINK;  // Tea
        drinks[2] = DrinkType.DRINK;  // Juice
        drinks[3] = DrinkType.EAT;    // Carrot
        drinks[4] = DrinkType.EAT;    // Ice-cream
        drinks[5] = DrinkType.DRINK;  // Milk
        drinks[6] = DrinkType.DRINK;  // Blackcurrant
        drinks[7] = DrinkType.DRINK;  // Water
        drinks[8] = DrinkType.DRINK;  // Regular
        drinks[9] = DrinkType.DRINK;  // Decaff
        drinks[10] = DrinkType.DRINK; // Latte
        drinks[11] = DrinkType.DRINK; // Mocha
        drinks[12] = DrinkType.DRINK; // Macchiato
        drinks[13] = DrinkType.DRINK; // Espresso
        drinks[14] = DrinkType.DRINK; // Filter
        drinks[15] = DrinkType.DRINK; // Iced
        drinks[16] = DrinkType.DRINK; // Cappuccino
        drinks[17] = DrinkType.DRINK; // Java
        drinks[18] = DrinkType.DRINK; // Tap
        drinks[19] = DrinkType.DRINK; // H*bbo Cola
        drinks[20] = DrinkType.ITEM;  // Camera
        drinks[21] = DrinkType.EAT;   // Hamburger
        drinks[22] = DrinkType.DRINK; // Lime H*bbo Soda
        drinks[23] = DrinkType.DRINK; // Beetroot H*bbo Soda
        drinks[24] = DrinkType.DRINK; // Bubble juice from 1999
        drinks[25] = DrinkType.DRINK; // Lovejuice

        // Public rooms send the localised handitem name instead of the drink ID
        if (carryName != null) {
            for (int i = 0; i <= 25; i++) {
                String externalDrinkName = TextsManager.getInstance().getValue("handitem" + i);

                if (externalDrinkName != null && externalDrinkName.equalsIgnoreCase(carryName)) {
                    carryId = i;
                }
            }
        }

        DrinkType type = null;

        // Not a valid drink ID
        if (carryId <= 0 || carryId > 25) {
            type = DrinkType.DRINK;
            useRawString = true;
        } else {
            type = drinks[carryId];
        }

        if (type == DrinkType.DRINK) {
            carryStatus = StatusType.CARRY_DRINK;
            useStatus = StatusType.USE_DRINK;
        }

        if (type == DrinkType.EAT) {
            carryStatus = StatusType.CARRY_FOOD;
            useStatus = StatusType.USE_FOOD;
        }

        if (type == DrinkType.ITEM) {
            carryStatus = StatusType.CARRY_ITEM;
            useStatus = StatusType.USE_ITEM;
        }

        this.removeStatus(StatusType.CARRY_ITEM);
        this.removeStatus(StatusType.CARRY_FOOD);
        this.removeStatus(StatusType.CARRY_DRINK);
        this.removeStatus(StatusType.DANCE);

        if (!useRawString) {
            if (carryStatus != StatusType.CARRY_ITEM) {
                this.setStatus(carryStatus, carryId, GameConfiguration.getInstance().getInteger("carry.timer.seconds"), useStatus, 12, 1);
            } else {
                this.setStatus(carryStatus, carryId); // Use camera for infinite time, don't switch between using and holding item.
            }
        } else {
            this.setStatus(carryStatus, carryName, GameConfiguration.getInstance().getInteger("carry.timer.seconds"), useStatus, 12, 1);
        }

        this.needsUpdate = true;
    }

    /**
     * Remove drinks, used for when going AFK.
     */
    public void removeDrinks() {
        if (this.containsStatus(StatusType.CARRY_FOOD) || this.containsStatus(StatusType.CARRY_DRINK)) {
            this.removeStatus(StatusType.CARRY_DRINK);
            this.removeStatus(StatusType.CARRY_FOOD);
            this.needsUpdate = true;
        }
    }

    /**
     * Handle chatting
     *
     * @param message the text to read for any gestures and to find animation length
     * @param chatMessageType the talk message type
     */
    public void talk(String message, CHAT_MESSAGE.ChatMessageType chatMessageType) {
        List<Player> recieveMessages = new ArrayList<>();

        if (this.entity.getType() == EntityType.PLAYER) {
            Player player = (Player) entity;

            for (Player sessions : room.getEntityManager().getPlayers()) {
                if (sessions.getIgnoredList().contains(player.getDetails().getName())) {
                    continue;
                }

                recieveMessages.add(sessions);
            }
        } else {
            recieveMessages.addAll(this.room.getEntityManager().getPlayers());
        }

        this.talk(message, chatMessageType, recieveMessages);
    }

    /**
     * Handle chatting.
     *
     * @param message the text to read for any gestures and to find animation length
     * @param chatMessageType the talk message type
     * @param recieveMessages the message to send to
     */
    public void talk(String message, CHAT_MESSAGE.ChatMessageType chatMessageType, List<Player> recieveMessages) {
        //message = WordfilterManager.filterSentence(message);
        if (chatMessageType != CHAT_MESSAGE.ChatMessageType.WHISPER) {
            List<Entity> entities = new ArrayList<>(recieveMessages);

            // Remove self from looking
            entities.remove(this.entity);

            // Make any users look towards player
            for (Entity entity : entities) {
                if (entity.getRoomUser().containsStatus(StatusType.AVATAR_SLEEP)) {
                    continue;
                }

                if (chatMessageType == CHAT_MESSAGE.ChatMessageType.CHAT) {
                    if (this.entity.getRoomUser().getPosition().getDistanceSquared(entity.getRoomUser().getPosition()) > 14) {
                        continue;
                    }
                }

                entity.getRoomUser().look(this.position);
            }
        }

        String[] words = message.split(" ");
        int talkDuration = 1;

        // Send mouth movements to room
        if (words.length <= 5) {
            talkDuration = words.length / 2;
        } else {
            talkDuration = 5;
        }

        if (words.length == 1) {
            talkDuration = message.length() / 10;
        }

        if (talkDuration > 0) {
            this.setStatus(StatusType.TALK, "", talkDuration, null, -1, -1);
            this.needsUpdate = true;
        }

        // Send gesture to room
        String gesture = this.getChatGesture(message);

        if (gesture != null) {
            this.setStatus(StatusType.GESTURE, gesture, 5, null, -1, -1);
            this.needsUpdate = true;
        }

        // Send talk message to room
        var chatMsg = new CHAT_MESSAGE(chatMessageType, this.instanceId, message);

        for (var player : recieveMessages) {
            player.send(chatMsg);
        }

        if (this.entity.getType() == EntityType.PLAYER) {
            if (chatMessageType != CHAT_MESSAGE.ChatMessageType.WHISPER) {
                BotManager.getInstance().handleSpeech((Player) this.entity, this.room, message);
                PetManager.getInstance().handleSpeech((Player) this.entity, this.room, message);
            }

            ChatManager.getInstance().queue((Player) this.entity, this.room, message, chatMessageType);
            this.timerManager.resetRoomTimer();
        }
    }

    /**
     * Gets the gesture type for the chat message
     *
     * @param message the text to read for any gestures and to find animation length
     */
    private String getChatGesture(String message) {
        String gesture = null;

        if (message.contains(":)")
                || message.contains(":-)")
                || message.contains(":p")
                || message.contains(":d")
                || message.contains(":D")
                || message.contains(";)")
                || message.contains(";-)")) {
            gesture = "sml";
        }

        if (gesture == null &&
                (message.contains(":s")
                        || message.contains(":(")
                        || message.contains(":-(")
                        || message.contains(":'("))) {
            gesture = "sad";
        }

        if (gesture == null &&
                (message.contains(":o")
                        || message.contains(":O"))) {
            gesture = "srp";
        }


        if (gesture == null &&
                (message.contains(":@")
                        || message.contains(">:("))) {
            gesture = "agr";
        }

        return gesture;
    }

    /**
     * Look towards a certain point.
     *
     * @param towards the coordinate direction to look towards
     */
    public void look(Position towards) {
        this.look(towards, false);
    }

    public void look(Position towards, boolean body) {
        if (this.isWalking) {
            return;
        }

        this.position.setHeadRotation(Rotation.getHeadRotation(this.position.getRotation(), this.position, towards));

        if (body) {
            int rotation = Rotation.calculateHumanDirection(
                    this.getPosition().getX(),
                    this.getPosition().getY(),
                    towards.getX(),
                    towards.getY());

            this.position.setHeadRotation(rotation);
            this.position.setBodyRotation(rotation);
        } else {
            this.timerManager.beginLookTimer();
        }

        this.needsUpdate = true;
    }

    /**
     * Force room user to wave
     */
    public void wave() {
        if (this.containsStatus(StatusType.WAVE)) {
            return;
        }

        if (this.containsStatus(StatusType.DANCE)) {
            this.removeStatus(StatusType.DANCE);
        }

        this.setStatus(StatusType.WAVE, "");

        if (!this.entity.getRoomUser().isWalking()) {
            this.room.send(new USER_STATUSES(List.of(this.entity)));
        }

        GameScheduler.getInstance().getService().schedule(new WaveTask(this.entity), 2, TimeUnit.SECONDS);
    }

    /**
     * Update new height.
     */
    public void updateNewHeight(Position position) {
        if (this.room == null) {
            return;
        }

        RoomTile tile = this.room.getMapping().getTile(position);

        if (tile == null) {
            return;
        }

        double height = tile.getWalkingHeight();
        double oldHeight = this.position.getZ();

        if (height != oldHeight) {
            this.position.setZ(height);
            this.needsUpdate = true;
        }
    }

    /**
     * Get the current tile the user is on.
     *
     * @return the room tile instance
     */
    public RoomTile getTile() {
        if (this.room == null) {
            return null;
        }

        return this.room.getMapping().getTile(this.position);
    }

    /**
     * Warps a user to a position, with the optional ability trigger an instant update.
     *
     * @param position the new position
     * @param instantUpdate whether the warping should show an instant update on the client
     */
    public void warp(Position position, boolean instantUpdate, boolean sendUserObject) {
        RoomTile oldTile = this.getTile();

        if (oldTile != null) {
            oldTile.removeEntity(this.entity);
        }

        if (this.nextPosition != null) {
            RoomTile nextTile = this.room.getMapping().getTile(this.nextPosition);

            if (nextTile != null) {
                nextTile.removeEntity(this.entity);
            }
        }

        this.position = position.copy();
        this.updateNewHeight(position);

        RoomTile newTile = this.getTile();

        if (newTile != null) {
            newTile.addEntity(this.entity);
        }

        if (instantUpdate && this.room != null) {
            if (sendUserObject) {
                this.room.send(new USER_OBJECTS(List.of(this.entity)));
            }

            this.room.send(new USER_STATUSES(List.of(this.entity)));

            if (oldTile != null) {
                this.invokeItem(oldTile.getPosition(), true);
            }
        }
    }

    /**
     * Contains status.
     *
     * @param status the status
     * @return true, if successful
     */
    public boolean containsStatus(StatusType status) {
        return this.statuses.containsKey(status.getStatusCode());
    }

    /**
     * Removes the status.
     *
     * @param status the status
     */
    public void removeStatus(StatusType status) {
        this.statuses.remove(status.getStatusCode());
    }

    /**
     * Sets the status.
     *
     * @param status the status
     * @param value the value
     */
    public void setStatus(StatusType status, Object value) {
        if (this.containsStatus(status)) {
            this.removeStatus(status);
        }

        this.statuses.put(status.getStatusCode(), new RoomUserStatus(status, value.toString()));
    }

    /**
     * Set a status with a limited lifetime, and optional swap to action every x seconds which lasts for
     * x seconds. Use -1 and 'null' for action and lifetimes to make it last indefinitely.
     *
     * @param status the status to add
     * @param value the status value
     * @param secLifetime the seconds of lifetime this status has in total
     * @param action the action to switch to
     * @param secActionSwitch the seconds to count until it switches to this action
     * @param secSwitchLifetime the lifetime the action lasts for before switching back.
     */
    public void setStatus(StatusType status, Object value, int secLifetime, StatusType action, int secActionSwitch, int secSwitchLifetime) {
        if (this.containsStatus(status)) {
            this.removeStatus(status);
        }

        this.statuses.put(status.getStatusCode(), new RoomUserStatus(status, value.toString(), secLifetime, action, secActionSwitch, secSwitchLifetime));
    }

    /**
     * Get if the entity is sitting on the ground, or on furniture which isn't a chair.
     *
     * @return true, if successful
     */
    public boolean isSittingOnGround() {
        if (this.getCurrentItem() == null || !this.getCurrentItem().hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP)) {
            return this.containsStatus(StatusType.SIT);
        }

        return false;
    }

    /**
     * Get if the entity is sitting on a chair.
     *
     * @return true, if successful.
     */
    public boolean isSittingOnChair() {
        if (this.getCurrentItem() != null) {
            return this.getCurrentItem().hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP);
        }

        return false;
    }

    /**
     * Get the status by status type.
     *
     * @param statusType the status type
     * @return the room user status instance
     */
    public RoomUserStatus getStatus(StatusType statusType) {
        if (this.statuses.containsKey(statusType.getStatusCode())) {
            return this.statuses.get(statusType.getStatusCode());
        }

        return null;
    }

    public Item getCurrentItem() {
        RoomTile tile = this.getTile();

        if (tile != null && tile.getHighestItem() != null) {
            return tile.getHighestItem();
        }

        return null;
    }


    public Entity getEntity() {
        return entity;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getGoal() {
        return goal;
    }

    public Position getNextPosition() {
        return nextPosition;
    }

    public void setNextPosition(Position nextPosition) {
        this.nextPosition = nextPosition;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public Map<String, RoomUserStatus> getStatuses() {
        return this.statuses;
    }

    public LinkedList<Position> getPath() {
        return path;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public void setWalking(boolean walking) {
        isWalking = walking;
    }

    public boolean isNeedsUpdate() {
        return needsUpdate;
    }

    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public RollingData getRollingData() {
        return rollingData;
    }

    public void setRollingData(RollingData rollingData) {
        this.rollingData = rollingData;
    }

    public RoomTimerManager getTimerManager() {
        return timerManager;
    }

    public boolean isWalkingAllowed() {
        return isWalkingAllowed;
    }

    public void setWalkingAllowed(boolean walkingAllowed) {
        isWalkingAllowed = walkingAllowed;
    }

    public Item getLastItemInteraction() {
        return lastItemInteraction;
    }

    public void setLastItemInteraction(Item lastItemInteraction) {
        this.lastItemInteraction = lastItemInteraction;
    }

    public boolean isRolling() {
        return this.rollingData != null;
    }

    public void setEnableWalkingOnStop(boolean enableWalkingOnStop) {
        this.enableWalkingOnStop = enableWalkingOnStop;
        this.isWalkingAllowed = !enableWalkingOnStop;
    }

}
