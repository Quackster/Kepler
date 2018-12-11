package org.alexdev.roseau.game.room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.TeleporterInteractor;
import org.alexdev.roseau.game.navigator.NavigatorRequest;
import org.alexdev.roseau.game.player.Bot;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.game.room.schedulers.*;
import org.alexdev.roseau.game.room.schedulers.events.*;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.OutgoingMessageComposer;
import org.alexdev.roseau.messages.outgoing.*;
import org.alexdev.roseau.server.messages.Response;

import com.google.common.collect.Lists;

public class Room {

    private int orderID = -1;
    private boolean disposed;

    private RoomData roomData;
    private RoomMapping roomMapping;

    private RoomEventScheduler roomEventScheduler;
    private RoomWalkScheduler roomWalkScheduler;

    private List<Entity> entities;

    private ConcurrentHashMap<Integer, Item> passiveObjects;
    private ConcurrentHashMap<Integer, Item> items;

    private List<Bot> bots;
    private ArrayList<RoomEvent> events;

    private ScheduledFuture<?> tickTask = null;
    private ScheduledFuture<?> eventTask = null;

    private List<Integer> rights;

    public Room() {
        this.roomData = new RoomData(this);
        this.roomMapping = new RoomMapping(this);

        this.roomEventScheduler = new RoomEventScheduler(this);
        this.roomWalkScheduler = new RoomWalkScheduler(this);

        this.entities = Lists.newArrayList();
        this.events = Lists.newArrayList();
    }

    public void load() throws Exception {

        if (this.roomData.getRoomType() == RoomType.PUBLIC && !this.roomData.isHidden()) {

            /*this.serverHandler = Class.forName("org.alexdev.roseau.server.netty.NettyServer")
					.asSubclass(IServerHandler.class)
					.getDeclaredConstructor(String.class)
					.newInstance(String.valueOf(this.roomData.getId()));*/

            Log.println("[ROOM] [" + this.roomData.getName() + "] Starting public room server on port: " + this.roomData.getServerPort());


            //this.serverHandler.setIp(Roseau.getServerIP());
            //this.serverHandler.setPort(this.roomData.getServerPort());
            //this.serverHandler.listenSocket();
        }


        if (this.roomData.getRoomType() == RoomType.PRIVATE) {
            this.rights = Roseau.getDao().getRoom().getRoomRights(this.roomData.getID());
            this.items = Roseau.getDao().getItem().getRoomItems(this.roomData.getID());
        } else {
            this.rights = Lists.newArrayList();
        }
    }

    public void firstPlayerEntry() {
        this.disposed = false;


        if (this.tickTask == null) {
            this.tickTask = Roseau.getGame().getScheduler().scheduleAtFixedRate(this.roomWalkScheduler, 0, 500, TimeUnit.MILLISECONDS);
        }

        this.passiveObjects = Roseau.getDao().getItem().getPublicRoomItems(this.roomData.getModelName(), this.roomData.getID());
        this.bots = Roseau.getDao().getRoom().getBots(this, this.roomData.getID());

        this.roomMapping.regenerateCollisionMaps();

        if (this.roomData.getModelName().equals("bar_b")) {
            this.registerNewEvent(new ClubMassivaDiscoEvent(this));
        }

        if (this.roomData.getModelName().equals("pool_b")) {
            this.registerNewEvent(new HabboLidoEvent(this));
        }

        if (this.bots.size() > 0) {
            this.entities.addAll(this.bots);
            this.registerNewEvent(new BotMoveRoomEvent(this));
        }

        this.registerNewEvent(new UserStatusEvent(this));
    }

    private void registerNewEvent(RoomEvent event) {

        if (this.eventTask == null) {
            this.eventTask = Roseau.getGame().getScheduler().scheduleAtFixedRate(this.roomEventScheduler, 0, 500, TimeUnit.MILLISECONDS);
        }

        this.events.add(event);

    }

    public void loadRoom(Player player) {

        if (this.roomData.getModel() != null) {
            this.loadRoom(player, this.roomData.getModel().getDoorPosition(), this.roomData.getModel().getDoorRot());
        } else {
            Log.println("Could not load door data for room model '" + this.roomData.getModelName() + "'");
        }
    }

    public void loadRoom(final Player player, Position door, int rotation) {

        if (player.getMainServerPlayer() == null) {
            if (!GameVariables.DEBUG_ENABLE) {
                player.send(new SYSTEMBROADCAST("Please reload client completely before entering rooms."));
                player.kick();
                return;
            }
        }

        RoomUser roomEntity = player.getRoomUser();

        roomEntity.setRoom(this);
        roomEntity.getStatuses().clear();

        if (this.roomData == null) {
            Log.println("null wot");

        }

        if (this.roomData.getModel() != null) {
            roomEntity.getPosition().setX(door.getX());
            roomEntity.getPosition().setY(door.getY());
            roomEntity.getPosition().setZ(door.getZ());
            roomEntity.getPosition().setRotation(rotation);
        }

        if (this.roomData.getModel() == null) {
            Log.println("Could not load heightmap for room model '" + this.roomData.getModelName() + "'");
        }	

        if (this.entities.size() > 0) {
            this.send(player.getRoomUser().getUsersComposer());
            player.getRoomUser().sendStatusComposer();
        } else {
            this.firstPlayerEntry();
        }

        if (this.roomData.getRoomType() == RoomType.PRIVATE) {

            player.getInventory().load();
            player.send(new ROOM_READY(this.roomData.getDescription()));

            int wallData = Integer.parseInt(this.roomData.getWall());
            int floorData = Integer.parseInt(this.roomData.getFloor());

            if (wallData > 0) {
                player.send(new FLATPROPERTY("wallpaper", this.roomData.getWall()));
            } else {
                player.send(new FLATPROPERTY("wallpaper", "201"));
            }

            if (floorData > 0) {
                player.send(new FLATPROPERTY("floor", this.roomData.getFloor()));
            } else {
                player.send(new FLATPROPERTY("floor", "0"));
            }	
        }

        this.refreshFlatPrivileges(player, true);

        if (this.roomData.getModel() != null) {
            player.send(new HEIGHTMAP(this.roomData.getModel().getHeightMap()));
        }

        player.send(new OBJECTS_WORLD(this.roomData.getModelName(), this.passiveObjects));
        player.send(new ACTIVE_OBJECTS(this));

        if (this.roomData.getRoomType() == RoomType.PRIVATE) {
            player.send(new ITEMS(this));
        }

        player.send(new USERS(this.entities));
        player.send(new STATUS(this.entities));

        player.send(player.getRoomUser().getUsersComposer());
        player.send(player.getRoomUser().getStatusComposer());

        this.entities.add(player);

        if (player.getMainServerPlayer() != null) {
            player.getMainServerPlayer().getMessenger().sendStatus();
        }

        roomEntity.resetAfkTimer();

        if (this.roomData.getRoomType() == RoomType.PRIVATE) {
            final Item item = this.roomMapping.getHighestItem(door.getX(), door.getY());

            if (item != null) {
                if (item.getDefinition().getBehaviour().isTeleporter()) {

                    TeleporterInteractor interactor = (TeleporterInteractor)item.getInteraction();
                    interactor.leaveTeleporter(player);

                    return;
                }
            }
        } else {

            // Show the players whether curtain is closed in pool changing booth or the pool lift door etc
            // for anyone new who enters the room
            for (Item item : this.passiveObjects.values()) {

                OutgoingMessageComposer composer = item.getCurrentProgram();

                if (composer != null) {
                    player.send(composer);
                }
            }
        }
    }

    public boolean ringDoorbell(Player player) {

        boolean received = false;

        for (Player rights : this.getPlayersWithRights()) {
            rights.send(new DOORBELL_RINGING(player.getDetails().getName()));
            received = true;
        }

        return received;
    }

    public boolean hasRights(Player user, boolean ownerCheckOnly) {

        if (user.hasPermission("room_all_rights")) {
            return true;
        }

        if (this.roomData.getOwnerID() == user.getDetails().getID()) {
            return true;
        } else {
            if (!ownerCheckOnly) {
                return this.rights.contains(Integer.valueOf(user.getDetails().getID()));
            }
        }

        return false;
    }

    public void giveUserRights(Player player) {

        if (this.rights.contains(Integer.valueOf(player.getDetails().getID()))) {
            return;
        }

        this.rights.add(Integer.valueOf(player.getDetails().getID()));
        this.refreshFlatPrivileges(player, false);
        this.roomData.saveRights();
    }

    public void removeUserRights(Player player) {

        if (!this.rights.contains(Integer.valueOf(player.getDetails().getID()))) {
            return;
        }

        this.rights.remove(Integer.valueOf(player.getDetails().getID()));
        this.refreshFlatPrivileges(player, false);
        this.roomData.saveRights();
    }

    public void refreshFlatPrivileges(Player player, boolean enterRoom) {

        if (player.getDetails().getRank() == 2) { // Bronze Hobba
            player.getRoomUser().setStatus("mod", " 1", true, -1);
        } else if (player.getDetails().getRank() == 3) { // Silver Hobba
            player.getRoomUser().setStatus("mod", " 2", true, -1);
        } else if (player.getDetails().getRank() == 4) { // Gold Hobba
            player.getRoomUser().setStatus("mod", " 3", true, -1);
        } else if (player.getDetails().getRank() == 5) { // Admin/owner
            player.getRoomUser().setStatus("mod", " A", true, -1);
        }

        if (this.roomData.getOwnerID() == player.getDetails().getID() || player.hasPermission("room_all_rights")) {
            player.send(new YOUAREOWNER());
            player.getRoomUser().setStatus("flatctrl", " useradmin", true, -1);

        } else if (this.hasRights(player, false) || this.roomData.hasAllSuperUser()) {
            player.send(new YOUARECONTROLLER());
            player.getRoomUser().setStatus("flatctrl", "", true, -1);
        } else {
            player.getRoomUser().removeStatus("flatctrl");
            player.getRoomUser().removeStatus("mod");
            player.send(new YOUARENOTCONTROLLER());
        }

        if (!enterRoom) {
            player.getRoomUser().setNeedUpdate(true);
        }
    }

    public void send(OutgoingMessageComposer response, boolean checkRights) {

        if (this.disposed) {
            return;
        }

        for (Player player : this.getPlayers()) {
            player.send(response);
        }
    }


    public void leaveRoom(Player player, boolean hotelView) {

        if (hotelView) {
            if (player.getPrivateRoomPlayer() != null) { 
                player.getPrivateRoomPlayer().getNetwork().close();
            }
        }

        if (this.entities != null) {
            this.entities.remove(player);
        }

        RoomUser roomUser = player.getRoomUser();		
        Item item = roomUser.getCurrentItem();

        if (item != null) {
            if (item.getDefinition().getSprite().equals("poolLift") || item.getDefinition().getSprite().equals("poolBooth")) {
                item.showProgram("open");
                item.unlockTiles();
            }
        }

        roomUser.dispose();

        this.send(new LOGOUT(player.getDetails().getName()));
        this.dispose();

        player.getInventory().dispose();

        if (player.getMainServerPlayer() != null) {
            player.getMainServerPlayer().getMessenger().sendStatus();
        }
    }

    public void dispose(boolean forceDisposal) {

        try {

            if (forceDisposal) {

                for (Player player : this.getPlayers()) {
                    this.leaveRoom(player, true);
                }

                this.clearData();
                this.entities = null;
                Roseau.getGame().getRoomManager().getLoadedRooms().remove(this.getData().getID());

            } else {

                if (this.disposed) {
                    return;
                }

                if (this.getPlayers().size() > 0) {
                    return;
                }

                this.clearData();

                if (Roseau.getGame().getPlayerManager().getByID(this.roomData.getOwnerID()) == null) {
                    if (this.roomData.getRoomType() == RoomType.PRIVATE) { 


                        this.entities = null;
                        this.disposed = true;

                        Roseau.getGame().getRoomManager().getLoadedRooms().remove(this.getData().getID());
                        this.roomData = null;

                    }
                }
            }

        } catch (Exception e) {
            Log.exception(e);
        }

    }

    private void clearData() {

        if (this.entities != null) {
            this.entities.clear();
        }

        if (this.bots != null) {
            this.bots.clear();
        }

        if (this.events != null) {
            this.events.clear();
        }

        if (this.tickTask != null) {
            this.tickTask.cancel(true);
            this.tickTask = null;
        }

        if (this.eventTask != null) {
            this.eventTask.cancel(true);
            this.eventTask = null;
        }
    }



    public void send(OutgoingMessageComposer response) {

        if (this.disposed) {
            return;
        }

        for (Player player : this.getPlayers()) {
            player.send(response);
        }
    }

    public List<Player> getPlayers() {

        List<Player> sessions = Lists.newArrayList();

        for (Entity entity : this.getEntities(EntityType.PLAYER)) {
            Player player = (Player)entity;
            sessions.add(player);
        }

        return sessions;
    }

    public List<Player> getPlayersWithRights() {

        List<Player> sessions = Lists.newArrayList();

        for (Player player : this.getPlayers()) {
            if (this.hasRights(player, false)) {
                sessions.add(player);
            }
        }

        return sessions;
    }


    public Player getPlayerByID(int ID) {

        for (Player player : this.getPlayers()) {
            if (player.getDetails().getID() == ID) {
                return player;
            }
        }

        return null;

    }

    public Player getPlayerByName(String name) {

        for (Player player : this.getPlayers()) {
            if (player.getDetails().getName().equals(name)) {
                return player;
            }
        }

        return null;

    }

    public List<Entity> getEntities(EntityType type) {
        List<Entity> e = new ArrayList<Entity>();

        for (Entity entity : this.entities) {
            if (entity.getType() == type) {
                e.add(entity);
            }
        }

        return e;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public ArrayList<RoomEvent> getEvents() {
        return events;
    }

    public RoomData getData() {
        return roomData;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void setDisposed(boolean disposed) {
        this.disposed = disposed;
    }

    public void save() {
        Roseau.getDao().getRoom().updateRoom(this);
    }

    public void dispose() {
        this.dispose(false);
    }

    public boolean isValidStep(Entity entity, Position current, Position neighbour, boolean isFinalMove) {

        if (this.roomData.getModel().invalidXYCoords(current.getX(), current.getY())) {
            return false;
        }

        if (this.roomData.getModel().invalidXYCoords(neighbour.getX(), neighbour.getY())) {
            return false;
        }

        if (this.roomData.getModel().isBlocked(current.getX(), current.getY())) {
            return false;
        }

        if (this.roomData.getModel().isBlocked(neighbour.getX(), neighbour.getY())) {
            return false;
        }

        double heightCurrent = this.roomData.getModel().getHeight(current);
        double heightNeighour = this.roomData.getModel().getHeight(neighbour);

        Item currentItem = this.roomMapping.getHighestItem(current.getX(), current.getY());
        Item nextItem = this.roomMapping.getHighestItem(neighbour.getX(), neighbour.getY());

        if (currentItem != null) {

            if (currentItem.getDefinition().getSprite().equals("poolEnter") || currentItem.getDefinition().getSprite().equals("poolExit")) {
                return entity.getDetails().getPoolFigure().length() > 0;
            }

            if (nextItem != null) {

                if (nextItem.getDefinition().getSprite().equals("poolQueue")) {
                    if (currentItem.getDefinition().getSprite().equals("poolQueue")) {
                        return true;
                    }
                }
            }

        } else if (nextItem != null) {

            if (nextItem.getDefinition().getSprite().equals("poolQueue")) {
                if (nextItem.getPosition().getX() == 21 && nextItem.getPosition().getY() == 9) {

                    if (!(entity.getDetails().getTickets() > 0)) {
                        return false;
                    }

                    return true;
                } else {
                    return false;
                }
            }
        }
        
        if (heightCurrent > heightNeighour) {
            if ((heightCurrent - heightNeighour) >= 1.5) {
                return false;
            }
        }

        if (heightNeighour > heightCurrent) {
            if ((heightNeighour - heightCurrent) >= 1.5) {
                return false;
            }
        }

        if (!current.isMatch(this.roomData.getModel().getDoorPosition())) {

            if (!this.roomMapping.isValidTile(entity, current.getX(), current.getY())) {
                return false;
            }

            if (!current.isMatch(entity.getRoomUser().getPosition())) {
                if (currentItem != null) {
                    if (!isFinalMove) {
                        return currentItem.getDefinition().getBehaviour().isCanStandOnTop();
                    }

                    if (isFinalMove) {
                        return currentItem.canWalk(entity, current);

                    }
                }
            }
        }

        return true;
    }


    public ConcurrentHashMap<Integer, Item> getItems() {
        return items;
    }

    public List<Item> getWallItems() {

        List<Item> items = Lists.newArrayList();

        for (Item item : this.items.values()) {
            if (item.getDefinition().getBehaviour().isOnWall()) {
                items.add(item);
            }
        }

        return items;
    }

    public RoomMapping getMapping() {
        return roomMapping;
    }


    public void setRoomMapping(RoomMapping roomMapping) {
        this.roomMapping = roomMapping;
    }

    public void serialise(Response response, NavigatorRequest request) {
        response.appendNewArgument(String.valueOf(this.roomData.getID()));
        response.appendPartArgument(this.roomData.getName());

        if (request != NavigatorRequest.PRIVATE_ROOMS) {
            if (this.roomData.showOwnerName()) {
                response.appendPartArgument(this.roomData.getOwnerName());
            } else {
                response.appendPartArgument("-");
            }
        } else {
            response.appendPartArgument(this.roomData.getOwnerName());
        }

        response.appendPartArgument(this.roomData.getState().toString());
        response.appendPartArgument("");//this.roomData.getPassword()); // password...
        response.appendPartArgument("floor1");
        response.appendPartArgument(Roseau.getServerIP());
        response.appendPartArgument(Roseau.getServerIP());
        response.appendPartArgument(String.valueOf(Roseau.getPrivateServerPort()));
        response.appendPartArgument(String.valueOf(this.roomData.getUsersNow()));
        response.appendPartArgument("null");
        response.appendPartArgument(this.roomData.getDescription());
    }

    public Item getItem(int id) {

        if (this.items.containsKey(id)) {
            return this.items.get(id);
        }

        return null;
    }

    public ConcurrentHashMap<Integer, Item> getPassiveObjects() {
        return passiveObjects;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public List<Bot> getBots() {
        return bots;
    }

    public List<Integer> getRights() {
        return rights;
    }
}
