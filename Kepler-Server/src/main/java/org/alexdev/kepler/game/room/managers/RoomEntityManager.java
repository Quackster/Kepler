package org.alexdev.kepler.game.room.managers;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.dao.mysql.RoomRightsDao;
import org.alexdev.kepler.dao.mysql.RoomVoteDao;
import org.alexdev.kepler.game.bot.BotManager;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.events.EventsManager;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.public_items.PublicItemParser;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.game.room.mapping.RoomTile;
import org.alexdev.kepler.game.room.tasks.TeleporterTask;
import org.alexdev.kepler.messages.outgoing.events.ROOMEEVENT_INFO;
import org.alexdev.kepler.messages.outgoing.rooms.FLATPROPERTY;
import org.alexdev.kepler.messages.outgoing.rooms.ROOM_READY;
import org.alexdev.kepler.messages.outgoing.rooms.ROOM_URL;
import org.alexdev.kepler.messages.outgoing.rooms.UPDATE_VOTES;
import org.alexdev.kepler.messages.outgoing.rooms.user.HOTEL_VIEW;
import org.alexdev.kepler.messages.outgoing.rooms.user.LOGOUT;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_OBJECTS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RoomEntityManager {
    private Room room;

    public RoomEntityManager(Room room) {
        this.room = room;
    }

    /**
     * Generates a unique ID for the entities in a room. Will be used for pets
     * and bots in future.
     *
     * @return the unique ID
     */
    public int generateUniqueId() {
        int uniqueId = ThreadLocalRandom.current().nextInt(0, 99999);

        while (getByInstanceId(uniqueId) != null) {
            uniqueId = generateUniqueId();
        }

        return uniqueId;
    }

    /**
     * Return the list of entities currently in this room by its
     * given class.
     *
     * @param entityClass the entity class
     * @return List<T extends Entity> list of entities
     */
    public <T extends Entity> List<T> getEntitiesByClass(Class<T> entityClass) {
        List<T> entities = new ArrayList<>();

        for (Entity entity : this.room.getEntities()) {
            if (entity.getClass().isAssignableFrom(entityClass)) {
                entities.add(entityClass.cast(entity));
            }
        }

        return entities;
    }

    /**
     * Return the list of players currently in this room by its
     * given class.
     *
     * @return List<Player> list of players
     */
    public List<Player> getPlayers() {
        return getEntitiesByClass(Player.class);
    }

    /**
     * Get an entity by instance id.
     *
     * @param instanceId the instance id to get by
     * @return the entity
     */
    public Entity getByInstanceId(int instanceId) {
        for (Entity entity : this.room.getEntities()) {
            if (entity.getRoomUser().getInstanceId() == instanceId) {
                return entity;
            }
        }

        return null;
    }

    /**
     * Get an entity by id.
     *
     * @param id the instance id to get by
     * @return the entity
     */
    public Entity getById(int id, EntityType entityType) {
        for (Entity entity : this.room.getEntities()) {
            if (entity.getDetails().getId() == id && entity.getType() == entityType) {
                return entity;
            }
        }

        return null;
    }

    /**
     * Adds a generic entity to the room.
     * Will send packets if the entity is a player.
     *
     * @param entity      the entity to add
     * @param destination the (optional) destination to take the user to when they enter
     */
    public void enterRoom(Entity entity, Position destination) {
        if (entity.getRoomUser().getRoom() != null) {
            entity.getRoomUser().getRoom().getEntityManager().leaveRoom(entity, false);
        }

        // If the room is not loaded, add room, as we intend to join it.
        if (!RoomManager.getInstance().hasRoom(this.room.getId()) && !this.room.isGameArena()) {
            RoomManager.getInstance().addRoom(this.room);
        }

        entity.getRoomUser().reset();
        entity.getRoomUser().setRoom(this.room);
        entity.getRoomUser().setInstanceId(this.generateUniqueId());

        Position entryPosition = this.room.getModel().getDoorLocation();

        if (destination != null) {
            entryPosition = destination.copy();
        }

        entity.getRoomUser().setPosition(entryPosition);

        if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) entity;
            GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

            if (gamePlayer != null && gamePlayer.isEnteringGame()) {
                entity.getRoomUser().setInstanceId(gamePlayer.getObjectId()); // Instance ID will always be player id
                entity.getRoomUser().setWalkingAllowed(false); // Block walking initially when joining game
            }
        } else {
            if (this.getPlayers().size() > 0) {
                this.room.send(new USER_OBJECTS(entity));
            }
        }

        this.room.getEntities().add(entity);
        this.room.getData().setVisitorsNow(this.room.getEntityManager().getPlayers().size());



        // From this point onwards we send packets for the user to enter
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;
        player.getRoomUser().setAuthenticateId(-1);

        this.tryInitialiseRoom(player);

        if (player.getRoomUser().getAuthenticateTelporterId() != -1) {
            Item teleporter = ItemDao.getItem(player.getRoomUser().getAuthenticateTelporterId());

            if (teleporter != null) {
                Item linkedTeleporter = this.room.getItemManager().getById(teleporter.getTeleporterId());

                if (linkedTeleporter != null) {
                    TeleporterTask teleporterTask = new TeleporterTask(linkedTeleporter, entity, this.room);
                    teleporterTask.run();

                    player.getRoomUser().setWalkingAllowed(true);
                    entity.getRoomUser().setPosition(linkedTeleporter.getPosition().copy());
                }
            }

            player.getRoomUser().setAuthenticateTelporterId(-1);
        }

        player.send(new ROOM_URL());
        player.send(new ROOM_READY(this.room.getId(), this.room.getModel().getName()));

        if (this.room.getData().getWallpaper() > 0) {
            player.send(new FLATPROPERTY("wallpaper", this.room.getData().getWallpaper()));
        }

        if (this.room.getData().getFloor() > 0) {
            player.send(new FLATPROPERTY("floor", this.room.getData().getFloor()));
        }



        // Don't let the room owner vote on it's own room
        boolean voted = this.room.isOwner(player.getDetails().getId()) || this.room.hasVoted(player.getDetails().getId());

        if (voted) {
            player.send(new UPDATE_VOTES(this.room.getData().getRating()));
        } else {
            player.send(new UPDATE_VOTES(-1));
        }

        // Send room event information
        player.send(new ROOMEEVENT_INFO(EventsManager.getInstance().getEventByRoomId(this.room.getId())));
        player.getMessenger().sendStatusUpdate();

        // Save increased visitor count of this room
        RoomDao.saveVisitors(this.room);

        GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

        if (gamePlayer != null && gamePlayer.isEnteringGame()) {
            gamePlayer.setEnteringGame(false);
        }
    }

    /**
     * Setup the room initially for room entry.
     */
    private void tryInitialiseRoom(Player player) {
         if (!this.room.isActive()) {
             if (!this.room.isGameArena()) {
                 this.room.getItems().clear();
                 this.room.getRights().clear();
                 this.room.getVotes().clear();

                 if (this.room.isPublicRoom()) {
                     this.room.getItems().addAll(PublicItemParser.getPublicItems(this.room.getId(), this.room.getModel().getId()));
                 } else {
                     this.room.getRights().addAll(RoomRightsDao.getRoomRights(this.room.getData()));
                     this.room.getVotes().putAll(RoomVoteDao.getRatings(this.room.getId()));
                 }

                 this.room.getItems().addAll(ItemDao.getRoomItems(this.room.getData()));
                 this.room.getItemManager().resetItemStates();
             }

             this.room.getMapping().regenerateCollisionMap();
             this.room.getTaskManager().startTasks();
        }
    }

    /**
     * Setup the room initially for room entry.
     */
    public void tryRoomEntry(Player player) {
        boolean isRoomActive = this.room.isActive();

        if (!this.room.isActive()) {
            this.room.setActive(true);
        };

        if (this.room.getModel().getModelTrigger() != null) {
            this.room.getModel().getModelTrigger().onRoomEntry(player, this.room, !isRoomActive);
        }

        // Load bot data if first entry
        if (!isRoomActive) {
            BotManager.getInstance().addBots(this.room);
        }
    }

    /**
     * Setup handler for the entity to leave room.
     *
     * @param entity the entity to leave
     */
    public void leaveRoom(Entity entity, boolean hotelView) {
        if (!this.room.getEntities().contains(entity)) {
            return;
        }

        // Set up trigger for leaving a current item
        if (entity.getRoomUser().getCurrentItem() != null) {
            if (entity.getRoomUser().getCurrentItem().getDefinition().getInteractionType().getTrigger() != null) {
                entity.getRoomUser().getCurrentItem().getDefinition().getInteractionType().getTrigger().onEntityLeave(entity, entity.getRoomUser(), entity.getRoomUser().getCurrentItem());
            }
        }

        this.room.getEntities().remove(entity);

        // Trigger for leaving room
        if (this.room.getModel().getModelTrigger() != null) {
            this.room.getModel().getModelTrigger().onRoomLeave(entity, this.room);
        }

        // Entity tile removal
        RoomTile tile = entity.getRoomUser().getTile();

        if (tile != null) {
            tile.removeEntity(entity);
        }

        if (entity.getRoomUser().getNextPosition() != null) {
            RoomTile nextTile = this.room.getMapping().getTile(entity.getRoomUser().getNextPosition());

            if (nextTile != null) {
                nextTile.removeEntity(entity);
            }
        }

        // From this point onwards we send packets for the user to leave
        this.room.getData().setVisitorsNow(this.room.getEntityManager().getPlayers().size());
        this.room.send(new LOGOUT(entity.getRoomUser().getInstanceId()));
        this.room.tryDispose();

        entity.getRoomUser().reset();

        // From this point onwards we send packets for the user to leave
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (hotelView) {
            player.send(new HOTEL_VIEW());
        }

        player.getMessenger().sendStatusUpdate();
        RoomDao.saveVisitors(this.room);

        // If we left room while in a game, leave the game
        GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

        if (gamePlayer != null && !gamePlayer.isEnteringGame()) {
            if (gamePlayer.getGame() != null) {
                gamePlayer.getGame().leaveGame(gamePlayer);
            } else {
                player.getRoomUser().setGamePlayer(null);
            }
        }
    }
}
