package org.alexdev.kepler.game.messenger;

import org.alexdev.kepler.game.events.EventsManager;
import org.alexdev.kepler.game.navigator.NavigatorManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.StringUtil;

public class MessengerUser {
    private int userId;
    private String username;
    private String figure;
    private String sex;
    private String motto;
    private long lastOnline;
    private boolean allowStalking;
    private boolean toRemove;
    private boolean toAdd;

    public MessengerUser(PlayerDetails details) {
        this.applyUserDetails(details.getId(), details.getName(), details.getFigure(), details.getMotto(), String.valueOf(details.getSex()), details.getLastOnline(), details.doesAllowStalking());
    }

    public MessengerUser(int userId, String username, String figure, String sex, String consoleMotto, long lastOnline, boolean allowStalking) {
        this.applyUserDetails(userId, username, figure, consoleMotto, sex, lastOnline, allowStalking);
    }

    /**
     * Geneic method for applying details, used from both constructors.
     *
     * @param userId the id of the user
     * @param username the name of the user
     * @param figure the figure of the user
     * @param consoleMotto the console motto of the user
     * @param sex the sex of the user
     * @param lastOnline the last time the user was online in Unix time
     */
    private void applyUserDetails(int userId, String username, String figure, String consoleMotto, String sex, long lastOnline, boolean allowStalking) {
        this.toRemove = false;
        this.userId = userId;
        this.username = StringUtil.filterInput(username, true);
        this.figure = StringUtil.filterInput(figure, true);
        this.sex = sex.toLowerCase().equals("f") ? "F" : "M";
        this.lastOnline = lastOnline;
        this.motto = StringUtil.filterInput(consoleMotto, true);
        this.allowStalking = allowStalking;
    }

    /**
     * Serialise the player, used for console search and friends list.
     *
     * @param response the response to serialise for
     */
    public void serialise(Player friend, NettyResponse response) {
        Player player = PlayerManager.getInstance().getPlayerById(this.userId);

        if (player != null) {
            this.figure = player.getDetails().getFigure();
            this.lastOnline = player.getDetails().getLastOnline();
            this.sex = Character.toString(player.getDetails().getSex());
            this.motto = player.getDetails().getMotto();
            this.allowStalking = player.getDetails().doesAllowStalking();
        }

        boolean isOnline = player != null;

        response.writeInt(this.userId);
        response.writeString(this.username);
        response.writeBool(this.sex.toLowerCase().equals("m"));

        response.writeBool(isOnline);
        response.writeBool(this.canFollowFriend(friend));

        response.writeString(isOnline ? this.figure : "");
        response.writeInt(0);
        response.writeString(this.motto);
        response.writeString(DateUtil.getDateAsString(this.lastOnline));
    }

    private boolean canFollowFriend(Player friend) {
        Player player = PlayerManager.getInstance().getPlayerById(this.userId);

        if (player == null) {
            return false;
        }

        if (player.getRoomUser().getRoom() == null) {
            return false;
        }

        Room room = player.getRoomUser().getRoom();

        // Don't allow follow into rooms that you cannot gain entry into normally
        return true;//(!room.getModel().getName().startsWith("bb_") && !room.getModel().getName().equals("snowwar"));
    }


    public int getUserId() {
        return this.userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public String getSex() {
        return sex;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    @Override
    public String toString() {
        return "[" + username + "," + motto + "," + figure + "," + sex + "," + lastOnline + "]";
    }
}
