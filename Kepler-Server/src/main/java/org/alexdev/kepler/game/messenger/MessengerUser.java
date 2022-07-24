package org.alexdev.kepler.game.messenger;

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
    private char sex;
    private String consoleMotto;
    private long lastOnline;
    private boolean fromSearch;

    public MessengerUser(PlayerDetails details) {
        this.applyUserDetails(details.getId(), details.getName(), details.getFigure(), details.getConsoleMotto(), String.valueOf(details.getSex()), details.getLastOnline());
    }

    public MessengerUser(PlayerDetails details, boolean fromSearch) {
        this.fromSearch = fromSearch;
        this.applyUserDetails(details.getId(), details.getName(), details.getFigure(), details.getConsoleMotto(), String.valueOf(details.getSex()), details.getLastOnline());
    }

    public MessengerUser(int userId, String username, String figure, String sex, String consoleMotto, long lastOnline) {
        this.applyUserDetails(userId, username, figure, consoleMotto, sex, lastOnline);
    }

    /**
     * Geneic method for applying details, used from both constructors.
     *
     * @param userId       the id of the user
     * @param username     the name of the user
     * @param figure       the figure of the user
     * @param consoleMotto the console motto of the user
     * @param sex          the sex of the user
     * @param lastOnline   the last time the user was online in Unix time
     */
    private void applyUserDetails(int userId, String username, String figure, String consoleMotto, String sex, long lastOnline) {
        this.userId = userId;
        this.username = StringUtil.filterInput(username, true);
        this.figure = StringUtil.filterInput(figure, true);
        this.sex = sex.toLowerCase().equals("f") ? 'F' : 'M';
        this.lastOnline = lastOnline;
        this.consoleMotto = StringUtil.filterInput(consoleMotto, true);
    }

    /**
     * Serialise the player, used for console search and friends list.
     *
     * @param response the response to serialise for
     */
    public void serialise(NettyResponse response) {
        Player user = PlayerManager.getInstance().getPlayerById(this.userId);

        if (user != null) {
            this.figure = user.getDetails().getFigure();
            this.lastOnline = user.getDetails().getLastOnline();
            this.sex = user.getDetails().getSex();
            this.consoleMotto = user.getDetails().getConsoleMotto();
        }

        response.writeInt(this.userId);
        response.writeString(this.username);
        response.writeBool(Character.toLowerCase(this.sex) == 'm');
        response.writeString(this.consoleMotto);

        boolean isOnline = (user != null);

        response.writeBool(isOnline);

        if (isOnline) {
            if (user.getRoomUser().getRoom() != null) {
                Room room = user.getRoomUser().getRoom();

                if (room.getData().getOwnerId() > 0) {
                    response.writeString("Floor1a");
                } else {
                    if(this.fromSearch) {
                        response.writeString("ENTERPRISESERVER");
                    } else {
                        response.writeString("");
                    }

                }
            } else {
                response.writeString("On hotel view");
            }
        } else {
            response.writeString(DateUtil.getDateAsString(this.lastOnline));
        }

        response.writeString(DateUtil.getDateAsString(this.lastOnline));
        response.writeString(this.figure);
    }

    public boolean canFollowFriend(Player friend) {
        Player player = PlayerManager.getInstance().getPlayerById(this.userId);

        if (player == null) {
            return false;
        }

        if (player.getRoomUser().getRoom() == null) {
            return false;
        }

        Room room = player.getRoomUser().getRoom();

        return (!room.getModel().getName().startsWith("bb_") && !room.getModel().getName().equals("snowwar"));
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

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String getConsoleMotto() {
        return consoleMotto;
    }

    public void setConsoleMotto(String consoleMotto) {
        this.consoleMotto = consoleMotto;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    @Override
    public String toString() {
        return "[" + username + "," + consoleMotto + "," + figure + "," + sex + "," + lastOnline + "]";
    }
}
