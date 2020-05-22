package org.alexdev.kepler.game.room;

import org.alexdev.kepler.dao.mysql.PetDao;
import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.dao.mysql.RoomVoteDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.navigator.NavigatorCategory;
import org.alexdev.kepler.game.navigator.NavigatorManager;
import org.alexdev.kepler.game.pets.Pet;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.managers.RoomEntityManager;
import org.alexdev.kepler.game.room.managers.RoomItemManager;
import org.alexdev.kepler.game.room.managers.RoomTaskManager;
import org.alexdev.kepler.game.room.mapping.RoomMapping;
import org.alexdev.kepler.game.room.models.RoomModel;
import org.alexdev.kepler.game.room.models.RoomModelManager;
import org.alexdev.kepler.messages.outgoing.rooms.UPDATE_VOTES;
import org.alexdev.kepler.messages.outgoing.rooms.moderation.YOUARECONTROLLER;
import org.alexdev.kepler.messages.outgoing.rooms.moderation.YOUAROWNER;
import org.alexdev.kepler.messages.outgoing.rooms.moderation.YOUNOTCONTROLLER;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.alexdev.kepler.util.schedule.FutureRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class Room {
    private RoomModel roomModel;
    private RoomData roomData;
    private RoomMapping roomMapping;
    private RoomEntityManager roomEntityManager;
    private RoomItemManager roomItemManager;
    private RoomTaskManager roomTaskManager;
    private FutureRunnable disposeRunnable;

    private boolean isActive;
    private boolean isGameArena;
    private int followRedirect;

    private List<Entity> entities;
    private List<Item> items;
    private List<Integer> rights;
    private Map<Integer, Integer> votes;

    public Room() {
        this.roomData = new RoomData(this);
        this.roomEntityManager = new RoomEntityManager(this);
        this.roomItemManager = new RoomItemManager(this);
        this.roomTaskManager = new RoomTaskManager(this);
        this.roomMapping = new RoomMapping(this);
        this.entities = new CopyOnWriteArrayList<>();
        this.items = new CopyOnWriteArrayList<>();
        this.rights = new CopyOnWriteArrayList<>();
        this.votes = new ConcurrentHashMap<>();
    }


    /**
     * Send a packet to all players.
     *
     * @param composer the message composer packet
     */
    public void send(MessageComposer composer) {
        this.send(composer, new ArrayList<>());
    }

    /**
     * Send a packet to all players but excludes to certain players when given a list
     *
     * @param composer the message composer packet
     */
    public void send(MessageComposer composer, List<Player> playerList) {
        for (Player player : this.roomEntityManager.getPlayers()) {
            player.send(composer);
        }
    }

    /**
     * Checks if the user id is the owner of the room.
     *
     * @param ownerId the owner id to check for
     * @return true, if successful
     */
    public boolean isOwner(int ownerId) {
        return this.roomData.getOwnerId() == ownerId;
    }

    /**
     * Get if the player has rights, include super users is enabled to true
     *
     * @param userId the user id to check if they have rights
     * @return true, if successful
     */
    public boolean hasRights(int userId) {
        return this.hasRights(userId, true);
    }

    /**
     * Get if the player has rights.
     *
     * @param userId the user id to check if they have rights
     * @param includeSuperUsers check if the room allows all users rights or not
     * @return true, if successful
     */
    public boolean hasRights(int userId, boolean includeSuperUsers) {
        if (this.isOwner(userId)) {
            return true;
        }

        if (includeSuperUsers) {
            if (this.roomData.allowSuperUsers()) {
                return true;
            }
        }

        if (this.rights.contains(userId)) {
            return true;
        }

        return false;
    }

    /**
     * Check if this certain user has voted
     *
     * @param userId
     * @return boolean indicating if the user has voted
     */
    public boolean hasVoted(int userId) {
        return this.votes.containsKey(userId);
    }

    /**
     * Add vote to this room
     *
     * @param answer chosen vote
     * @param userId user that is voting
     */
    public void addVote(int answer, int userId) {
        // Add vote to in-memory structure
        this.votes.put(userId, answer);

        // Re-calculate sum of all ratings
        int sum = this.votes.values().stream().mapToInt(Integer::intValue).sum();

        if (sum < 0) {
            sum = 0;
        }

        this.roomData.setRating(sum);
        RoomDao.saveRating(this.getId(), sum);

        // Send new vote count to all player entities
        for (Player p : this.roomEntityManager.getPlayers()) {
            boolean voted = this.hasVoted(p.getDetails().getId());

            // Only send new vote count to users who did vote
            if (voted || this.isOwner(p.getDetails().getId())) {
                p.send(new UPDATE_VOTES(this.roomData.getRating()));
            }
        }

        RoomVoteDao.vote(userId, this.roomData.getId(), answer);
    }

    /**
     * Refresh the room rights for the user.
     *
     * @param player the player to refresh the rights for
     */
    public void refreshRights(Player player) {
        if (hasRights(player.getDetails().getId())) {
            player.send(new YOUARECONTROLLER());
        } else {
            player.send(new YOUNOTCONTROLLER());
        }

        String rightsValue = "";

        if (isOwner(player.getDetails().getId()) || player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            player.send(new YOUAROWNER());
            rightsValue = "useradmin";
        }

        player.getRoomUser().removeStatus(StatusType.FLAT_CONTROL);

        if (hasRights(player.getDetails().getId()) || isOwner(player.getDetails().getId()) || player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
            player.getRoomUser().setStatus(StatusType.FLAT_CONTROL, rightsValue);
        }

        player.getRoomUser().setNeedsUpdate(true);
    }

    /**
     * Try to dispose room, it will happen when there's no users
     * in the room.
     *
     * @return if the room was successfully disposed
     */
    public boolean tryDispose() {
        Room room = this;

        if (this.roomEntityManager.getPlayers().size() > 0) {
            return false;
        }

        // If there's an existing dispose runnable, delete it.
        if (this.disposeRunnable != null) {
            this.disposeRunnable.cancelFuture();
            this.disposeRunnable = null;
        }

        // Add 60 second delay when disposing rooms so that there's less stress if the room wants to be reloaded instantly after leaving.
        this.disposeRunnable = new FutureRunnable() {
            @Override
            public void run() {
                if (roomEntityManager.getPlayers().size() > 0) {
                    return;
                }

                roomItemManager.resetItemStates();

                for (Pet pet : room.getEntityManager().getEntitiesByClass(Pet.class)) {
                    PetDao.saveCoordinates(pet.getDetails().getId(),
                            pet.getRoomUser().getPosition().getX(),
                            pet.getRoomUser().getPosition().getY(),
                            pet.getRoomUser().getPosition().getRotation());
                }

                isActive = false;
                roomTaskManager.stopTasks();

                items.clear();
                rights.clear();
                votes.clear();
                entities.clear();

                RoomManager.getInstance().removeRoom(roomData.getId());
            }
        };

        int defaultSeconds = GameConfiguration.getInstance().getInteger("room.dispose.timer.seconds");

        if (!GameConfiguration.getInstance().getBoolean("room.dispose.timer.enabled")) {
            defaultSeconds = 0;
        }

        // Schedule new dispose runnable
        this.disposeRunnable.setFuture(GameScheduler.getInstance().getService().schedule(this.disposeRunnable, defaultSeconds, TimeUnit.SECONDS));
        return true;
    }

    /**
     * Get the entity manager for this room.
     *
     * @return the entity manager
     */
    public RoomEntityManager getEntityManager() {
        return this.roomEntityManager;
    }

    /**
     * Get the item manager for this room.
     *
     * @return the item manager
     */
    public RoomItemManager getItemManager() {
        return this.roomItemManager;
    }

    /**
     * Get the task manager for this room.
     *
     * @return the task manager
     */
    public RoomTaskManager getTaskManager() {
        return this.roomTaskManager;
    }

    /**
     * Get the mapping manager for this room.
     *
     * @return the room mapping manager
     */
    public RoomMapping getMapping() {
        return this.roomMapping;
    }

    /**
     * Get the room data for this room.
     *
     * @return the room data
     */
    public RoomData getData() {
        return this.roomData;
    }

    /**
     * Get the room model instance.
     *
     * @return the room model
     */
    public RoomModel getModel() {
        if (this.roomModel != null) {
            return this.roomModel;
        }

        return RoomModelManager.getInstance().getModel(this.roomData.getModel());
    }

    /**
     * Set the room model, override the instance
     */
    public void setRoomModel(RoomModel roomModel) {
        this.roomModel = roomModel;
    }

    /**
     * Get the {@link NavigatorCategory} for this room.
     *
     * @return the navigator category
     */
    public NavigatorCategory getCategory() {
        return NavigatorManager.getInstance().getCategoryById(this.roomData.getCategoryId());
    }

    /**
     * Get the entire list of entities in the room.
     *
     * @return the list of entities
     */
    public List<Entity> getEntities() {
        return this.entities;
    }

    /**
     * Get the entire list of items in the room.
     *
     * @return the list of items
     */
    public List<Item> getItems() {
        return this.items;
    }

    /**
     * Get a list of user ids with room rights.
     *
     * @return the room rights list
     */
    public List<Integer> getRights() {
        return this.rights;
    }

    /**
     * Get a map of votes
     *
     * @return map of votes
     */
    public Map<Integer, Integer> getVotes() {
        return this.votes;
    }

    /**
     * Get whether the room is a public room or not.
     *
     * @return true, if successful
     */
    public boolean isPublicRoom() {
        return this.roomData.getOwnerId() == 0;
    }

    /**
     * Check if this room is for club members only
     *
     * @return true, if successful
     */
    public boolean isClubOnly() {
        if (!this.isPublicRoom()) {
            return false;
        }

        // 8 is Club Only category (TODO: create a club_only column in room categories table)
        if (this.getCategory().getId() == 8) {
            return true;
        }

        return false;
    }

    /**
     * Get the room id of this room.
     */
    public int getId() {
        return this.roomData.getId();
    }

    /**
     * Get if the room is active (has players in it).
     *
     * @return true, if successful
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * Set if the room is active, if it's the first player who joined, etc.
     *
     * @param active the active flag
     */
    public void setActive(boolean active) {
        this.isActive = active;
    }

    /**
     * Gets the main room id for room following, used for when someone follows
     * a friend into a room which requires a user to walk to it from entering the main room.
     *
     * @return the follow redirect room id
     */
    public int getFollowRedirect() {
        return this.followRedirect;
    }

    /**
     * Set the follow redirect room id.
     *
     * @param followRedirect the room id to set
     */
    public void setFollowRedirect(int followRedirect) {
        this.followRedirect = followRedirect;
    }

    /**
     * Get whether this room is a game arena (a battleball or snowstorm room)
     *
     * @return true, if successful
     */
    public boolean isGameArena() {
        return isGameArena;
    }

    /**
     * Set whether this room is a game arena (a battleball or snowstorm room)
     *
     * @param gameArena the flag on whether this is a game arena or not
     */
    public void setGameArena(boolean gameArena) {
        isGameArena = gameArena;
    }
}
