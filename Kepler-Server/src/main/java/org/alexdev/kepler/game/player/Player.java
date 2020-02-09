package org.alexdev.kepler.game.player;

import io.netty.util.AttributeKey;
import org.alexdev.kepler.Kepler;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.dao.mysql.SettingsDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.club.ClubSubscription;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.fuserights.FuserightsManager;
import org.alexdev.kepler.game.inventory.Inventory;
import org.alexdev.kepler.game.messenger.Messenger;
import org.alexdev.kepler.game.room.entities.RoomPlayer;
import org.alexdev.kepler.messages.outgoing.club.CLUB_GIFT;
import org.alexdev.kepler.messages.outgoing.handshake.AVAILABLE_SETS;
import org.alexdev.kepler.messages.outgoing.handshake.LOGIN;
import org.alexdev.kepler.messages.outgoing.handshake.RIGHTS;
import org.alexdev.kepler.messages.outgoing.moderation.USER_BANNED;
import org.alexdev.kepler.messages.outgoing.openinghours.INFO_HOTEL_CLOSING;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.messages.outgoing.user.HOTEL_LOGOUT;
import org.alexdev.kepler.messages.outgoing.user.HOTEL_LOGOUT.LogoutReason;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.NettyPlayerNetwork;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Player extends Entity {
    public static final AttributeKey<Player> PLAYER_KEY = AttributeKey.valueOf("Player");

    private final NettyPlayerNetwork network;
    private final PlayerDetails details;
    private final RoomPlayer roomEntity;

    private Set<String> ignoredList;

    private Logger log;
    private Messenger messenger;
    private Inventory inventory;

    private boolean loggedIn;
    private boolean disconnected;
    private boolean pingOK;

    public Player(NettyPlayerNetwork nettyPlayerNetwork) {
        this.network = nettyPlayerNetwork;
        this.details = new PlayerDetails();
        this.roomEntity = new RoomPlayer(this);
        this.ignoredList = new HashSet<>();
        this.log = LoggerFactory.getLogger("Connection " + this.network.getConnectionId());
        this.pingOK = true;
        this.disconnected = false;
    }

    /**
     * Login handler for player
     */
    public void login() {
        this.log = LoggerFactory.getLogger("Player " + this.details.getName()); // Update logger to show name
        this.loggedIn = true;
        this.pingOK = true;

        PlayerManager.getInstance().disconnectSession(this.details.getId()); // Kill other sessions with same id
        PlayerManager.getInstance().addPlayer(this); // Add new connection
        PlayerDao.saveLastOnline(this.getDetails());

        if (GameConfiguration.getInstance().getBoolean("reset.sso.after.login")) {
            PlayerDao.clearSSOTicket(this.details.getId()); // Protect against replay attacks
        }

        SettingsDao.updateSetting("players.online", String.valueOf(PlayerManager.getInstance().getPlayers().size()));

        this.messenger = new Messenger(this.details);
        this.inventory = new Inventory(this);

        // Bye bye!
        var banned = this.getDetails().isBanned();

        if (banned != null) {
            this.send(new USER_BANNED(banned.getKey()));
            GameScheduler.getInstance().getService().schedule(this::kickFromServer, 1, TimeUnit.SECONDS);
            return;
        }

        // Update user IP address
        String ipAddress = NettyPlayerNetwork.getIpAddress(this.getNetwork().getChannel());

        if (!PlayerDao.getLatestIp(this.details.getId()).equals(ipAddress)) {
            PlayerDao.logIpAddress(this.getDetails().getId(), ipAddress);
        }

        this.details.loadBadges();
        this.details.resetNextHandout();

        this.send(new LOGIN());
        this.refreshFuserights();

        if (GameConfiguration.getInstance().getBoolean("welcome.message.enabled")) {
            String alertMessage = GameConfiguration.getInstance().getString("welcome.message.content");
            alertMessage = alertMessage.replace("%username%", this.details.getName());

            this.send(new ALERT(alertMessage));
        }

        if (PlayerManager.getInstance().isMaintenance()) {
            this.send(new INFO_HOTEL_CLOSING(PlayerManager.getInstance().getMaintenanceAt()));
        }

        if (ClubSubscription.isGiftDue(this)) {
            this.send(new CLUB_GIFT(1));
        }

        this.messenger.sendStatusUpdate();
        ClubSubscription.refreshBadge(this);
    }

    /**
     * Refresh club for player.
     */
    public void refreshClub() {
        if (this.details.hasClubSubscription()) {
            this.send(new AVAILABLE_SETS("[" + GameConfiguration.getInstance().getString("users.figure.parts.club") + "]"));
        }

        ClubSubscription.refreshBadge(this);
        ClubSubscription.sendHcDays(this);
    }

    /**
     * Send fuseright permissions for player.
     */
    public void refreshFuserights() {
        List<Fuseright> fuserights = FuserightsManager.getInstance().getFuserightsForRank(this.details.getRank());

        if (this.getDetails().hasClubSubscription()) {
            fuserights.addAll(FuserightsManager.getInstance().getClubFuserights());
        }

        fuserights.removeIf(fuse -> !fuse.getFuseright().startsWith("fuse_"));
        this.send(new RIGHTS(fuserights));
    }

    /**
     * Check if the player has a permission for a rank.
     *
     * @param fuse the permission
     * @return true, if successful
     */
    @Override
    public boolean hasFuse(Fuseright fuse) {
        return FuserightsManager.getInstance().hasFuseright(fuse, this.details);
    }

    /**
     * Send a response to the player
     *
     * @param response the response
     */
    public void send(MessageComposer response) {
        this.network.send(response);
    }

    /**
     * Send a object to the player
     *
     * @param object the object to send
     */
    public void sendObject(Object object) {
        this.network.send(object);
    }


    /**
     * Send a queued response to the player
     *
     * @param response the response
     */
    public void sendQueued(MessageComposer response) {
        this.network.sendQueued(response);
    }

    /**
     * Flush queue
     */
    public void flush() {
        this.network.flush();
    }

    /**
     * Get the messenger instance for the player
     *
     * @return the messenger instance
     */
    public Messenger getMessenger() {
        return messenger;
    }

    @Override
    public PlayerDetails getDetails() {
        return this.details;
    }

    @Override
    public RoomPlayer getRoomUser() {
        return this.roomEntity;
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    /**
     * Get the player logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return this.log;
    }

    /**
     * Get the network handler for the player
     *
     * @return the network handler
     */
    public NettyPlayerNetwork getNetwork() {
        return this.network;
    }

    /**
     * Get if the player has logged in or not.
     *
     * @return true, if they have
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Get if the connection has timed out or not.
     *
     * @return false, if it hasn't.
     */
    public boolean isPingOK() {
        return pingOK;
    }

    /**
     * Get if the socket has been disconnected.
     *
     * @return true, if it has
     */
    public boolean isDisconnected() {
        return disconnected;
    }

    /**
     * Set if the connection has timed out or not.
     *
     * @param pingOK the value to determine of the connection has timed out
     */
    public void setPingOK(boolean pingOK) {
        this.pingOK = pingOK;
    }

    /**
     * Get rid of the player from the server.
     */
    public void kickFromServer() {
        try {
            this.network.send(new HOTEL_LOGOUT(LogoutReason.DISCONNECT));
            this.network.disconnect();

            this.dispose();
        } catch (Exception ignored) {
            // Ignore
        }
    }

    /**
     * Dispose player when disconnect happens.
     */
    @Override
    public void dispose() {
        try {
            if (this.loggedIn) {
                if (this.roomEntity.getRoom() != null) {
                    this.roomEntity.getRoom().getEntityManager().leaveRoom(this, false);
                }

                PlayerManager.getInstance().removePlayer(this);

                PlayerDao.saveLastOnline(this.getDetails());
                SettingsDao.updateSetting("players.online", String.valueOf(PlayerManager.getInstance().getPlayers().size()));

                this.messenger.sendStatusUpdate();
            }

            this.disconnected = true;
            this.loggedIn = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Set<String> getIgnoredList() {
        return ignoredList;
    }

    public int getVersion() {
        return Kepler.getServer().getConnectionRule(this.network.getPort()).getVersion();
    }
}
