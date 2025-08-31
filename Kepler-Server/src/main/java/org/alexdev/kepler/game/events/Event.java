package org.alexdev.kepler.game.events;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomData;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.util.DateUtil;

public class Event {
    private int roomId;
    private int userId;
    private int categoryId;
    private String name;
    private String description;
    private long expire;

    /***
     * The event constructor.
     *
     * @param roomId the room id the event is hosted in
     * @param userId the user id hosting the event
     * @param categoryId the category of the event
     * @param name the name of the event
     * @param description the event description
     * @param started the unix timestamp of when the event started
     */
    public Event(int roomId, int userId, int categoryId, String name, String description, long started) {
        this.roomId = roomId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.expire = started;
    }
    /**
     * Get whether the event has expired.
     *
     * @return true, if successful
     */
    public boolean isExpired() {
        return DateUtil.getCurrentTimeSeconds() > this.expire;
    }

    /**
     * Get the room id the event is being hosted in.
     *
     * @return the room id the event is hosted in
     */
    public int getRoomId() {
        return roomId;
    }

    /**
     * Get the room instance this event is being hosted for.
     */
    public int getPlayersInEvent() {
        Room room = RoomManager.getInstance().getRoomById(this.roomId);

        if (room != null) {
            return room.getEntityManager().getPlayers().size();
        }

        return 0;
    }

    /**
     * Get room data.
     */
    public RoomData getRoomData() {
        return RoomManager.getInstance().getRoomById(this.roomId).getData();
    }

    /**
     * Get the id of the user hosting the event.
     *
     * @return the event hoster id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Get player details
     */
    public PlayerDetails getUserInfo() {
        return PlayerManager.getInstance().getPlayerData(this.userId);
    }


    /**
     * Get the category id for this event.
     *
     * @return the catgeory id
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * Set the category id for this event.
     *
     * @param categoryId the category id
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Get the event hoster.
     *
     * @return the event hoster
     */
    public PlayerDetails getEventHoster() {
        return PlayerManager.getInstance().getPlayerData(this.userId);
    }

    /**
     * Get the event name.
     *
     * @return the event name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the new name for this event
     *
     * @param name the event name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the event description.
     *
     * @return the event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the new description for this event.
     *
     * @param description the event description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the time the event started.
     *
     * @return the time the event started
     */
    public long getExpireTime() {
        return expire;
    }

    /**
     * Get the time the event started as a formatted date.
     *
     * @return the date formatted
     */
    public String getStartedDate() {
        return DateUtil.getDateAsString(this.expire - EventsManager.getEventLifetime());
    }

}