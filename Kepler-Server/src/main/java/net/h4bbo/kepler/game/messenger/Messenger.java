package net.h4bbo.kepler.game.messenger;

import net.h4bbo.kepler.dao.mysql.MessengerDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.incoming.messenger.FRIENDLIST_UPDATE;
import net.h4bbo.kepler.messages.outgoing.messenger.CONSOLE_MOTTO;
import net.h4bbo.kepler.messages.outgoing.messenger.FRIEND_REQUEST;
import net.h4bbo.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Messenger {
    private List<MessengerCategory> messengerCategories;
    private Map<Integer, MessengerUser> friends;
    private Map<Integer, MessengerUser> requests;
    private Map<Integer, MessengerMessage> offlineMessages;

    private BlockingQueue<MessengerUser> friendsUpdate;
    private BlockingQueue<MessengerUser> friendsAdded;
    private BlockingQueue<MessengerUser> friendsRemoved;

    private String persistentMessage;
    private int friendsLimit;
    private boolean allowsFriendRequests;
    private MessengerUser user;
    private Room followed;

    public Messenger(PlayerDetails details) {
        this.user = new MessengerUser(details);
        this.persistentMessage = details.getConsoleMotto();
        this.friends = MessengerDao.getFriends(details.getId());
        this.requests = MessengerDao.getRequests(details.getId());
        this.offlineMessages = MessengerDao.getUnreadMessages(details.getId());
        this.allowsFriendRequests = details.isAllowFriendRequests();
        this.messengerCategories = MessengerDao.getCategories(details.getId());

        this.friendsUpdate = new LinkedBlockingQueue<>();
        this.friendsAdded = new LinkedBlockingQueue<>();
        this.friendsRemoved = new LinkedBlockingQueue<>();

        if (details.hasClubSubscription()) {
            this.friendsLimit = GameConfiguration.getInstance().getInteger("messenger.max.friends.club");
        } else {
            this.friendsLimit = GameConfiguration.getInstance().getInteger("messenger.max.friends.nonclub");
        }
    }

    /**
     * Sends the status update when a friend enters or leaves a room, logs in or disconnects.
     */
    public void sendStatusUpdate() {
        if (this.user == null) {
            return;
        }

        for (var user : this.friends.values()) {
            int userId = user.getUserId();

            Player friend = PlayerManager.getInstance().getPlayerById(userId);

            if (friend != null && friend.getMessenger() != null) {
                friend.getMessenger().queueFriendUpdate(this.user);
                //new FRIENDLIST_UPDATE().handle(friend, null);
            }
        }
    }

    /**
     * Get if the user already has a request from this user id.
     *
     * @param userId the user id to check for
     * @return true, if successful
     */
    public boolean hasRequest(int userId) {
        return this.getRequest(userId) != null;
    }

    /**
     * Get if the user already has a friend with this user id.
     *
     * @param userId the user id to check for
     * @return true, if successful
     */
    public boolean hasFriend(int userId) {
        return this.getFriend(userId) != null;
    }

    public void addFriend(MessengerUser friend) {
        MessengerDao.removeRequest(friend.getUserId(), this.user.getUserId());
        MessengerDao.newFriend(friend.getUserId(), this.user.getUserId());

        this.requests.remove(friend.getUserId());
        this.friends.put(friend.getUserId(), friend);
    }

    public void addRequest(MessengerUser requester) {
        MessengerDao.newRequest(requester.getUserId(), this.user.getUserId());
        this.requests.put(requester.getUserId(), requester);

        Player requested = PlayerManager.getInstance().getPlayerById(this.user.getUserId());

        if (requested != null) {
            requested.send(new FRIEND_REQUEST(requester));
        }
    }

    public void declineRequest(MessengerUser requester) {
        MessengerDao.removeRequest(requester.getUserId(), this.user.getUserId());
        this.requests.remove(requester);
    }

    public void declineAllRequests() {
        MessengerDao.removeAllRequests(this.user.getUserId());
        this.requests.clear();
    }

    /**
     * getPersistentMessage
     * Get if the friend limit is reached. Limit is dependent upon club subscription
     *
     * @return true, if limit reached
     */
    public boolean isFriendsLimitReached() {
        return this.friends.size() >= this.getFriendsLimit();
    }

    public int getFriendsLimit() {
        return this.friendsLimit;
    }

    public void setPersistentMessage(String persistentMessage) {
        this.persistentMessage = persistentMessage;

        Player player = PlayerManager.getInstance().getPlayerById(this.user.getUserId());

        if (player != null) {
            player.getDetails().setConsoleMotto(persistentMessage);
            player.send(new CONSOLE_MOTTO(persistentMessage));
        }

        PlayerDao.saveMotto(player.getDetails().getId(), persistentMessage);
    }

    public String getPersistentMessage() {
        return this.persistentMessage;
    }

    /**
     * Get the messenger user instance with this user id.
     *
     * @param userId the user id to check for
     * @return the messenger user instance
     */
    public MessengerUser getRequest(int userId) {
        return this.requests.get(userId);
    }

    /**
     * Get the messenger user instance with this user id.
     *
     * @param userId the user to check for
     * @return the messenger user instance
     */
    public MessengerUser getFriend(int userId) {
        return this.friends.get(userId);
    }

    /**
     * Remove friend from friends list
     *
     * @param userId
     * @return boolean indicating success
     */
    public boolean removeFriend(int userId) {
        this.friends.remove(userId);

        MessengerDao.removeFriend(userId, this.user.getUserId());

        return true;
    }

    /**
     * Get the list of offline messages.
     *
     * @return the list of offline messages
     */
    public Map<Integer, MessengerMessage> getOfflineMessages() {
        return offlineMessages;
    }

    /**
     * Get the list of friends.
     *
     * @return the list of friends
     */
    public List<MessengerUser> getFriends() {
        return new ArrayList<>(this.friends.values());
    }

    public MessengerUser getMessengerUser() {
        return this.user;
    }

    /**
     * Get the list of friends.
     *
     * @return the list of friends
     */
    public List<MessengerUser> getRequests() {
        return new ArrayList<>(this.requests.values());
    }

    public boolean isAllowsFriendRequests() {
        return allowsFriendRequests;
    }

    /**
     * Gets the queue for the next friends came online update.
     *
     * @return the queue
     */
    public BlockingQueue<MessengerUser> getFriendsUpdate() {
        return friendsUpdate;
    }

    /**
     * Adds a user friend left to the events update console queue, removes any previous mentions of this friend.
     *
     * @param friend the friend to update
     */
    public void queueFriendUpdate(MessengerUser friend) {
        if (this.friendsAdded.stream().anyMatch(f -> friend.getUserId() == friend.getUserId())) {
            return;
        }

        if (this.friendsRemoved.stream().anyMatch(f -> friend.getUserId() == friend.getUserId())) {
            return;
        }

        this.friendsUpdate.removeIf(f -> f.getUserId() == friend.getUserId());
        this.friendsUpdate.add(friend);
    }

    public void hasFollowed(Room friendRoom) {
        this.followed = friendRoom;
    }

    public Room getFollowed() {
        return followed;
    }

    public List<MessengerCategory> getCategories() {
        return messengerCategories;
    }

    public void setFriendsLimit(int friendsLimit) {
        this.friendsLimit = friendsLimit;
    }

    public void setAllowsFriendRequests(boolean allowsFriendRequests) {
        this.allowsFriendRequests = allowsFriendRequests;
    }

    public MessengerUser getUser() {
        return user;
    }

    public void setUser(MessengerUser user) {
        this.user = user;
    }
}
