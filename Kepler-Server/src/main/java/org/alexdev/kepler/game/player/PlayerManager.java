package org.alexdev.kepler.game.player;

import com.goterl.lazysodium.interfaces.PwHash;
import org.alexdev.kepler.Kepler;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.messenger.Messenger;
import org.alexdev.kepler.game.player.register.RegisterDataType;
import org.alexdev.kepler.game.player.register.RegisterValue;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.texts.TextsManager;
import org.alexdev.kepler.messages.outgoing.openinghours.INFO_HOTEL_CLOSED;
import org.alexdev.kepler.messages.outgoing.openinghours.INFO_HOTEL_CLOSING;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.ServerConfiguration;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PlayerManager {
    private static PlayerManager instance;
    private List<Player> players;

    private long timeUntilNextReset;
    private long dailyPlayerPeak;

    private boolean isMaintenanceShutdown;
    private Duration maintenanceAt;

    private ScheduledFuture<?> shutdownTimeout;

    public PlayerManager() {
        this.players = new CopyOnWriteArrayList<>();
    }

    /**
     * Checks and sets the daily player peak.
     */
    public void checkPlayerPeak() {
        if (DateUtil.getCurrentTimeSeconds() > this.timeUntilNextReset) {
            this.timeUntilNextReset = DateUtil.getCurrentTimeSeconds() + TimeUnit.DAYS.toSeconds(1);
            this.dailyPlayerPeak = PlayerManager.getInstance().getPlayers().size();
        } else {
            int newSize = PlayerManager.getInstance().getPlayers().size();

            if (newSize > this.dailyPlayerPeak) {
                this.dailyPlayerPeak = newSize;
            }
        }

    }

    /**
     * Get a player by user id.
     *
     * @param userId the user id to get with
     * @return the player, else null if not found
     */
    public Player getPlayerById(int userId) {
        for (Player player : this.players) {
            if (player.getDetails().getId() == userId) {
                return player;
            }
        }

        return null;
    }

    /**
     * Get the player by name.
     *
     * @param username the name to get with
     * @return the player, else null if not found
     */
    public Player getPlayerByName(String username) {
        for (Player player : this.players) {
            if (player.getDetails().getName().equalsIgnoreCase(username)) {
                return player;
            }
        }

        return null;
    }

    /**
     * Get a player data by user id.
     *
     * @param userId the user id to get with
     * @return the player data, else if offline will query the database
     */
    public PlayerDetails getPlayerData(int userId) {
        Player player = getPlayerById(userId);

        if (player != null) {
            return player.getDetails();
        }

        return PlayerDao.getDetails(userId);
    }

    /**
     * Get a player data by username.
     *
     * @param username the username to get with
     * @return the player data, else if offline will query the database
     */
    public PlayerDetails getPlayerData(String username) {
        Player player = getPlayerByName(username);

        if (player != null) {
            return player.getDetails();
        }

        return PlayerDao.getDetails(username);
    }

    public Messenger getMessengerData(int userId) {
        Player player = getPlayerById(userId);

        if (player != null) {
            return player.getMessenger();
        }

        return new Messenger(this.getPlayerData(userId));
    }

    public Messenger getMessengerData(String username) {
        Player player = getPlayerByName(username);

        if (player != null) {
            return player.getMessenger();
        }

        PlayerDetails details = this.getPlayerData(username);

        if (details == null) {
            return null;
        }

        return new Messenger(details);
    }

    /**
     * Remove player from map, this is handled automatically when
     * the socket is closed.
     *
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        if (player.getDetails().getName() == null) {
            return;
        }

        this.players.remove(player);
    }

    /**
     * Remove player from map, this is handled automatically when
     * the player is logged in.
     *
     * @param player the player to remove
     */
    public void addPlayer(Player player) {
        if (player.getDetails() == null) {
            return;
        }

        this.players.add(player);
    }

    /**
     * Disconnect a session by user id.
     *
     * @param userId the user id of the session to disconnect
     */
    public void disconnectSession(int userId) {
        for (Player player : this.players) {
            if (player.getDetails().getId() == userId) {
                player.kickFromServer();
            }
        }
    }

    /**
     * Start shutdown timeout
     *
     * @param maintenanceAt when to shutdown
     */
    public void planMaintenance(Duration maintenanceAt) {
        // Interrupt current timeout to set new maintenance countdown
        if (this.shutdownTimeout != null) {
            this.shutdownTimeout.cancel(true);
        }

        // Start timeout that will trigger the shutdown hook
        this.shutdownTimeout = GameScheduler.getInstance().getService().schedule(() -> System.exit(0), maintenanceAt.toMillis(), TimeUnit.MILLISECONDS);

        // Let other Kepler components know we are in maintenance mode
        this.isMaintenanceShutdown = true;
        this.maintenanceAt = maintenanceAt;

        // Notify all users of shutdown timeout
        for (Player p : this.players) {
            p.send(new INFO_HOTEL_CLOSING(maintenanceAt));
        }
    }

    /**
     * Cancel shutdown timeout
     */
    public void cancelMaintenance() {
        // Cancel current timeout
        this.shutdownTimeout.cancel(true);

        // Let other Kepler components know we are no longer in maintenance mode
        this.isMaintenanceShutdown = false;

        // Notify all users maintenance has been cancelled
        for (Player p : this.players) {
            p.send(new ALERT(TextsManager.getInstance().getValue("maintenance_cancelled")));
        }
    }

    public void sendAll(MessageComposer composer) {
        for (Player p : this.players) {
            p.send(composer);
        }
    }

    /**
     * Close and dispose all users.
     */
    public void dispose() {
        for (Player p : this.players) {
            // Send fancy maintenance alert if we're shutting down
            p.send(new INFO_HOTEL_CLOSED(LocalTime.now(), false));

            // Now disconnect the player
            p.kickFromServer();
        }
    }

    /**
     * Get the collection of players on the server.
     *
     * @return the collection of players
     */
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Get the collection of active players on the server.
     *
     * @return the collection of active players
     */
    public Collection<Player> getActivePlayers() {
        List<Player> activePlayers = new ArrayList<>();
        for (Player player : PlayerManager.getInstance().getPlayers()) {
            if (player.getRoomUser().getRoom() == null) {
                continue;
            }

            if (player.getRoomUser().containsStatus(StatusType.AVATAR_SLEEP)) {
                continue;
            }

            activePlayers.add(player);
        }

        return activePlayers;
    }

    /**
     * Create password hash
     *
     * @param password password to hash
     * @return hashed password
     * @throws Exception
     */
    public String createPassword(String password) throws Exception {
        if (ServerConfiguration.getStringOrDefault("password.hashing.library", "argon2").equalsIgnoreCase("argon2")) {
            byte[] pw = password.getBytes();
            byte[] outputHash = new byte[PwHash.STR_BYTES];
            PwHash.Native pwHash = (PwHash.Native) Kepler.getLibSodium();
            boolean success = pwHash.cryptoPwHashStr(
                    outputHash,
                    pw,
                    pw.length,
                    PwHash.OPSLIMIT_INTERACTIVE,
                    PwHash.MEMLIMIT_INTERACTIVE
            );

            if (!success) {
                throw new Exception("Password creation was a failure!");
            }

            return new String(outputHash).replace((char) 0 + "", "");
        } else {
            return password;
        }
    }

    /**
     * Get values for registering.
     *
     * @return values
     */
    public LinkedHashMap<Integer, RegisterValue> getRegisterValues() {
        var registerValues = new LinkedHashMap<Integer, RegisterValue>();
        registerValues.put(1, new RegisterValue("parentagree", 1, RegisterDataType.BOOLEAN));
        registerValues.put(2, new RegisterValue("name", 2, RegisterDataType.STRING));
        registerValues.put(3, new RegisterValue("password", 3, RegisterDataType.STRING));
        registerValues.put(4, new RegisterValue("figure", 4, RegisterDataType.STRING));
        registerValues.put(5, new RegisterValue("sex", 5, RegisterDataType.STRING));
        registerValues.put(6, new RegisterValue("customData", 6, RegisterDataType.STRING));
        registerValues.put(7, new RegisterValue("email", 7, RegisterDataType.STRING));
        registerValues.put(8, new RegisterValue("birthday", 8, RegisterDataType.STRING));
        registerValues.put(9, new RegisterValue("directMail", 9, RegisterDataType.BOOLEAN));
        registerValues.put(10, new RegisterValue("has_read_agreement", 10, RegisterDataType.BOOLEAN));
        registerValues.put(11, new RegisterValue("isp_id", 11, RegisterDataType.STRING));
        registerValues.put(12, new RegisterValue("partnersite", 12, RegisterDataType.STRING));
        registerValues.put(13, new RegisterValue("oldpassword", 13, RegisterDataType.STRING));
        return registerValues;
    }

    public Object getRegisterValue(LinkedHashMap<Integer, RegisterValue> values, String label) {
        for (var value : values.values()) {
            if (value.getLabel().equals(label))
                return value.getDataType() == RegisterDataType.STRING ? value.getValue() : value.getFlag();
        }

        return null;
    }

    /**
     * Get daily player peak
     *
     * @return the daily player peak
     */
    public long getDailyPlayerPeak() {
        return this.dailyPlayerPeak;
    }

    /**
     * Get duration until shutdown
     *
     * @return duration until shutdown
     */
    public Duration getMaintenanceAt() {
        return this.maintenanceAt;
    }

    /**
     * Get maintenance shutdown status
     *
     * @return the maintenance shutdown status
     */
    public boolean isMaintenance() {
        return this.isMaintenanceShutdown;
    }

    /**
     * Gets the instance
     *
     * @return the instance
     */
    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }

        return instance;
    }
}
