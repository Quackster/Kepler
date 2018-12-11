package org.alexdev.kepler.game.messenger;

import org.alexdev.kepler.dao.mysql.MessengerDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.incoming.messenger.FRIENDLIST_UPDATE;
import org.alexdev.kepler.messages.outgoing.messenger.CONSOLE_MOTTO;
import org.alexdev.kepler.messages.outgoing.messenger.FRIEND_REQUEST;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Messenger {
    private Map<Integer, MessengerUser> friends;
    private Map<Integer, MessengerUser> requests;
    private Map<Integer, MessengerMessage> offlineMessages;
    private String persistentMessage;
    private int friendsLimit;
    private boolean allowsFriendRequests;
    private MessengerUser user;

    public Messenger(PlayerDetails details) {
        this.user = new MessengerUser(details);
        this.persistentMessage = details.getConsoleMotto();
        this.friends = MessengerDao.getFriends(details.getId());
        this.requests = MessengerDao.getRequests(details.getId());
        this.offlineMessages = MessengerDao.getUnreadMessages(details.getId());
        this.allowsFriendRequests = details.isAllowFriendRequests();

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
        for (var user : this.friends.values()) {
            int userId = user.getUserId();

            Player friend = PlayerManager.getInstance().getPlayerById(userId);

            if (friend != null) {
                new FRIENDLIST_UPDATE().handle(friend, null);
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
        MessengerDao.removeRequest(friend, this.user);
        MessengerDao.newFriend(friend, this.user);

        this.requests.remove(friend.getUserId());
        this.friends.put(friend.getUserId(), friend);
    }

    public void addRequest(MessengerUser requester) {
        MessengerDao.newRequest(requester, this.user);
        this.requests.put(requester.getUserId(), requester);

        Player requested = PlayerManager.getInstance().getPlayerById(this.user.getUserId());

        if (requested != null) {
            requested.send(new FRIEND_REQUEST(requester));
        }
    }

    public void declineRequest(MessengerUser requester) {
        MessengerDao.removeRequest(requester, this.user);
        this.requests.remove(requester);
    }

    public void declineAllRequests() {
        MessengerDao.removeAllRequests(this.user);
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

        PlayerDao.saveMotto(player.getDetails());
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
}