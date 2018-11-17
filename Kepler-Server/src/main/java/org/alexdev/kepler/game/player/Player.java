package org.alexdev.kepler.game.player;

import io.netty.util.AttributeKey;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.club.ClubSubscription;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.inventory.Inventory;
import org.alexdev.kepler.game.messenger.Messenger;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.moderation.FuserightsManager;
import org.alexdev.kepler.game.room.entities.RoomPlayer;
import org.alexdev.kepler.messages.outgoing.handshake.*;
import org.alexdev.kepler.messages.outgoing.openinghours.*;
import org.alexdev.kepler.messages.outgoing.user.*;
import org.alexdev.kepler.messages.outgoing.user.HOTEL_LOGOUT.*;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.NettyPlayerNetwork;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.alexdev.kepler.util.config.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Player extends Entity {
    public static final AttributeKey<Player> PLAYER_KEY = AttributeKey.valueOf("Player");

    private final NettyPlayerNetwork network;
    private final PlayerDetails details;
    private final RoomPlayer roomEntity;

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

        this.details.loadBadges();
        this.details.resetNextHandout();

        this.messenger = new Messenger(this.details);
        this.inventory = new Inventory(this);

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

        this.messenger.sendStatusUpdate();
    }

    /**
     * Refresh club for player.
     */
    public void refreshClub() {
        if (!this.details.hasClubSubscription()) {
            // If the database still thinks we have Habbo club even after it expired, reset it back to 0.
            if (this.details.getClubExpiration() > 0) {
                //this.details.setFirstClubSubscription(0);
                this.details.setClubExpiration(0);
                this.details.getBadges().remove("HC1"); // If their HC ran out, remove badge.
                this.details.getBadges().remove("HC2"); // No gold badge when not subscribed.

                this.refreshFuserights();
                PlayerDao.saveSubscription(this.details);
            }
        } else {
            if (!this.details.getBadges().contains("HC1")) {
                this.details.getBadges().add("HC1");
            }

            if (this.details.hasGoldClubSubscription()) {
                if (!this.details.getBadges().contains("HC2")) {
                    this.details.getBadges().add("HC2");
                }
            }
        }

        ClubSubscription.refreshSubscription(this);
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
        if (this.loggedIn) {
            if (this.roomEntity.getRoom() != null) {
                this.roomEntity.getRoom().getEntityManager().leaveRoom(this, false);
            }

            PlayerDao.saveLastOnline(this.getDetails());
            PlayerManager.getInstance().removePlayer(this);
            
            this.messenger.sendStatusUpdate();
        }

        this.disconnected = true;
        this.loggedIn = false;
    }
}
