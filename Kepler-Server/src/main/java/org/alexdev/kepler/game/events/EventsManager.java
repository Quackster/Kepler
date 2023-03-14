package org.alexdev.kepler.game.events;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.dao.mysql.EventsDao;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EventsManager {
    private static EventsManager instance;
    private List<Event> eventList;

    public EventsManager() {
        EventsDao.removeExpiredEvents();

        this.eventList = EventsDao.getEvents();
        this.removeExpiredEvents();
    }

    /**
     * This method is used to call the create event method.
     *
     * @param player the player who created the event
     * @param category the category of the event
     * @param name the name of the event
     * @param description the description of the event
     */
    public Event createEvent(Player player, int category, String name, String description) throws SQLException {
        long expireTime = DateUtil.getCurrentTimeSeconds() + EventsManager.getEventLifetime();

        Event event = new Event(
                player.getRoomUser().getRoom().getId(),
                player.getDetails().getId(),
                category,
                name,
                description,
                expireTime
        );

        EventsDao.addEvent(
                event.getRoomId(),
                event.getUserId(),
                event.getCategoryId(),
                event.getName(),
                event.getDescription(),
                event.getExpireTime()
        );

        this.eventList.add(event);
        return event;
    }

    /**
     * Get whether a player can create a room event or not.
     *
     * @param player the player to check for
     * @return true, if successful
     */
    public boolean canCreateEvent(Player player) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return false;
        } else {
            if (room.isPublicRoom()) {
                return false;
            }

            if (!room.isOwner(player.getDetails().getId())) {
                return false;
            }
        }

        if (EventsManager.getInstance().isHostingEvent(player.getDetails().getId())) {
            return false;
        }

        if (EventsManager.getInstance().hasEvent(room.getId())) {
            return false;
        }

        return true;
    }

    /**
     * Get events by category.
     *
     * @param categoryId the category to get the events for
     * @return the list of events
     */
    public List<Event> getEvents(int categoryId) {
        if (categoryId == 0) { // 0 is the hottest events
            var events = EventsManager.getInstance().getEventList();
            events.sort(Comparator.comparingInt(Event::getPlayersInEvent).reversed());
            return events;
        }

        return this.eventList.stream().filter(event -> event.getCategoryId() == categoryId && !event.isExpired()).collect(Collectors.toList());
    }

    /**
     * Get whether a room event exists already.
     *
     * @param roomId the room id to check for
     * @return true, if successful
     */
    public boolean hasEvent(int roomId) {
        return getEventByRoomId(roomId) != null;
    }

    /**
     * Get event by room id
     *
     * @param roomId the roomId to get the event for
     */
    public Event getEventByRoomId(int roomId) {
        var optional = this.eventList.stream().filter(event -> event.getRoomId() == roomId && !event.isExpired()).findFirst();
        return optional.orElse(null);
    }

    /**
     * Gets if a user is hosting an event already.
     *
     * @param userId the user id to check
     * @return true, if successful
     */
    public boolean isHostingEvent(int userId) {
        var optional = this.eventList.stream().filter(event -> event.getUserId() == userId && !event.isExpired()).findFirst();
        return optional.isPresent();

    }

    /**
     * Purge expired events.
     */
    public void removeExpiredEvents() {
        List<Event> expiredEvents = this.eventList.stream().filter(Event::isExpired).collect(Collectors.toList());
        EventsDao.removeEvents(expiredEvents);
        this.eventList.removeIf(Event::isExpired);
    }

    /**
     * Get the event lifetime.
     *
     * @return the event lifetime
     */
    public static long getEventLifetime() {
        return TimeUnit.MINUTES.toSeconds(GameConfiguration.getInstance().getInteger("events.expiry.minutes"));
    }

    /**
     * Remove an event.
     *
     * @param event the event to remove
     */
    public void removeEvent(Event event) {
        this.eventList.removeIf(e -> e.getRoomId() == event.getRoomId());
        EventsDao.removeEvent(event);
    }

    /**
     * Get the list of all active events.
     *
     * @return the list of events.
     */
    public List<Event> getEventList() {
        return this.eventList.stream().filter(event -> !event.isExpired()).collect(Collectors.toList());
    }

    /**
     * Get the {@link EventsManager} instance
     *
     * @return the item manager instance
     */
    public static EventsManager getInstance() {
        if (instance == null) {
            instance = new EventsManager();
        }

        return instance;
    }
}
