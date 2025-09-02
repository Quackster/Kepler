package net.h4bbo.kepler.game.player;

import net.h4bbo.kepler.dao.mysql.BanDao;
import net.h4bbo.kepler.dao.mysql.GroupDao;
import net.h4bbo.kepler.dao.mysql.GroupMemberDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.game.ban.BanType;
import net.h4bbo.kepler.game.groups.Group;
import net.h4bbo.kepler.game.groups.GroupMember;
import net.h4bbo.kepler.game.wordfilter.WordfilterManager;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.kepler.util.StringUtil;
import net.h4bbo.kepler.util.config.GameConfiguration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.TimeUnit;

public class PlayerDetails {
    // Basic info
    private int id;
    private String username;
    private String email;
    private String figure;
    private String poolFigure;
    private String motto;
    private String sex;
    private String ssoTicket;
    private String machineId;

    // Currencies
    private int credits;
    private int pixels;
    private long lastPixelsTime;
    private int tickets;
    private int film;
    private PlayerRank rank;

    // Club
    private long firstClubSubscription;
    private long clubExpiration;

    // Settings
    private boolean allowStalking;
    private int selectedRoomId;
    private boolean allowFriendRequests;
    private boolean onlineStatusVisible;
    private boolean profileVisible;
    private boolean wordFilterEnabled;
    private boolean tradeEnabled;
    private boolean soundEnabled;

    // Timestamps
    private long nextHandout;
    private long lastOnline;
    private long joinDate;

    private boolean isOnline;
    private String createdAt;
    private int favouriteGroupId;
    private boolean receiveNews;
    private long tradeBanExpiration;
    private String birthday;
    private GroupMember groupMember;
    private String consoleMotto;

    public PlayerDetails() {
    }


    /**
     * Fill the player data for the entity.
     * @param id the id to add
     * @param username the username
     * @param figure the figure
     * @param poolFigure the pool figure
     * @param credits the credits
     * @param motto the motto
     * @param sex the sex
     * @param tickets the tickets
     * @param film the film
     * @param rank the rank
     * @param lastOnline the last time they were online in a unix timestamp
     * @param firstClubSubscription the club subscribed date in a unix timestamp
     * @param clubExpiration the club expiration date in a unix timestamp
     * @param allowStalking allow stalking/following
     * @param soundEnabled allow playing sound from client
     */
    public void fill(int id, String username, String figure, String poolFigure, int credits, String email, String motto, String consoleMotto,
                     String sex, String ssoTicket, int tickets, int film, int rank, long lastOnline, long joinDate,
                     String machineId, long firstClubSubscription,
                     long clubExpiration, boolean allowStalking, int selectedRoom, boolean allowFriendRequests, boolean onlineStatusVisible,
                     boolean profileVisible, boolean wordFilterEnabled, boolean tradeEnabled, boolean soundEnabled, long tradeBanExpiration,
                     boolean receiveNews, boolean isOnline, int favouriteGroupId, String createdAt) {
        this.id = id;
        this.username = StringUtil.filterInput(username, true);
        this.figure = StringUtil.filterInput(figure, true); // Format: hd-180-1.ch-255-70.lg-285-77.sh-295-74.fa-1205-91.hr-125-31.ha-1016-
        this.poolFigure = StringUtil.filterInput(poolFigure, true); // Format: ch=s02/238,238,238
        this.motto = WordfilterManager.filterSentence(StringUtil.filterInput(motto, true));
        this.consoleMotto = consoleMotto;
        //this.consoleMotto = StringUtil.filterInput(consoleMotto, true);
        this.email = email;
        this.sex = sex.toLowerCase().equals("f") ? "F" : "M";
        this.ssoTicket = ssoTicket;
        this.lastPixelsTime = DateUtil.getCurrentTimeSeconds() + TimeUnit.MINUTES.toSeconds(15);
        this.credits = credits;
        this.tickets = tickets;
        this.film = film;
        this.rank = PlayerRank.getRankForId(rank);
        this.lastOnline = lastOnline;
        this.joinDate = joinDate;
        this.machineId = machineId;
        this.firstClubSubscription = firstClubSubscription;
        this.clubExpiration = clubExpiration;
        this.allowStalking = allowStalking;
        this.selectedRoomId = selectedRoom;
        this.allowFriendRequests = allowFriendRequests;
        this.onlineStatusVisible = onlineStatusVisible;
        this.profileVisible = profileVisible;
        this.wordFilterEnabled = wordFilterEnabled;
        this.tradeEnabled = tradeEnabled;
        this.soundEnabled = soundEnabled;
        this.tradeBanExpiration = tradeBanExpiration;
        this.isOnline = isOnline;
        this.favouriteGroupId = favouriteGroupId;
        this.receiveNews = receiveNews;
        this.createdAt = createdAt;

        if (this.credits < 0) {
            this.credits = 0;
        }

        if (this.tickets < 0) {
            this.tickets = 0;
        }

        if (this.film < 0) {
            this.film = 0;
        }
    }

    public void fill(int id, String username, String figure, String motto, String sex) {
        this.id = id;
        this.username = username;
        this.figure = figure;
        this.motto = motto;
        this.sex = sex;
        this.createdAt = "";
    }

    public boolean hasClubSubscription() {
        if (this.clubExpiration != 0) {
            if (DateUtil.getCurrentTimeSeconds() < this.clubExpiration) {
                return true;
            }
        }

        return false;
    }

    public Pair<String, Long> isBanned() {
        var userBanCheck = BanDao.hasBan(BanType.USER_ID, String.valueOf(this.id));

        if (userBanCheck != null) {
            return userBanCheck;
        }

        var machineBanCheck = BanDao.hasBan(BanType.MACHINE_ID, this.machineId);

        if (machineBanCheck != null) {
            return machineBanCheck;
        }

        var ipBanCheck = BanDao.hasBan(BanType.IP_ADDRESS, PlayerDao.getLatestIp(this.id));

        if (ipBanCheck != null) {
            return ipBanCheck;
        }

        return null;
    }


    public void resetNextHandout() {
        if (GameConfiguration.getInstance().getInteger("daily.credits.amount") > 0) {
            this.nextHandout = 0;//DateUtil.getCurrentTimeSeconds() + GameConfiguration.getInstance().getInteger("daily.credits.wait.time");
        } else {
            TimeUnit unit = TimeUnit.valueOf(GameConfiguration.getInstance().getString("credits.scheduler.timeunit"));
            this.nextHandout = DateUtil.getCurrentTimeSeconds() + unit.toSeconds(GameConfiguration.getInstance().getInteger("credits.scheduler.interval"));
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return username;
    }

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public String getPoolFigure() {
        return poolFigure;
    }

    public void setPoolFigure(String poolFigure) {
        this.poolFigure = poolFigure;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public int getFilm() {
        return film;
    }

    public void setFilm(int film) {
        this.film = film;
    }

    public PlayerRank getRank() {
        return this.rank;
    }

    public void setRank(PlayerRank rank) {
        this.rank = rank;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    public long getFirstClubSubscription() {
        return firstClubSubscription;
    }

    public void setFirstClubSubscription(long firstClubSubscription) {
        this.firstClubSubscription = firstClubSubscription;
    }

    public long getClubExpiration() {
        return clubExpiration;
    }

    public void setClubExpiration(long clubExpiration) {
        this.clubExpiration = clubExpiration;
    }

    public boolean doesAllowStalking() {
        return allowStalking;
    }

    public void setAllowStalking(boolean allowStalking) {
        this.allowStalking = allowStalking;
    }

    public boolean getSoundSetting() {
        return soundEnabled;
    }

    public void setSoundSetting(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public long getNextHandout() {
        return nextHandout;
    }

    public void setNextHandout(long nextHandout) {
        this.nextHandout = nextHandout;
    }

    public boolean isAllowFriendRequests() {
        return allowFriendRequests;
    }

    public String getSsoTicket() {
        return ssoTicket;
    }

    public void setSsoTicket(String ssoTicket) {
        this.ssoTicket = ssoTicket;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public boolean canSelectRoom() {
        return this.selectedRoomId == 0;
    }

    public boolean hasSelectedRoom() {
        return this.selectedRoomId > 0;
    }

    public int getSelectedRoomId() {
        return selectedRoomId;
    }

    public void setSelectedRoomId(int selectedRoomId) {
        this.selectedRoomId = selectedRoomId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOnline() {
        if (this.isOnlineStatusVisible()) {
            return isOnline;
        }

        return false;
    }

    public String getFormattedLastOnline() {
        return DateUtil.getDate(this.lastOnline, DateUtil.SHORT_DATE);
    }

    public String formatLastOnline(String format) {
        return DateUtil.getDate(this.lastOnline, format);
    }

    public String formatJoinDate(String format) {
        return DateUtil.getDate(this.joinDate, format);
    }

    public String getCreatedAt() {
        return createdAt.split(" ")[0];
    }

    public long getJoinDate() {
        return joinDate;
    }

    public boolean isOnlineStatusVisible() {
        return onlineStatusVisible;
    }

    public void setOnlineStatusVisible(boolean onlineStatusVisible) {
        this.onlineStatusVisible = onlineStatusVisible;
    }

    public boolean isProfileVisible() {
        return profileVisible;
    }

    public boolean isWordFilterEnabled() {
        return wordFilterEnabled;
    }

    public boolean isTradeEnabled() {
        return tradeEnabled;
    }

    public void setTradeEnabled(boolean tradeEnabled) {
        this.tradeEnabled = tradeEnabled;
    }

    public long getTradeBanExpiration() {
        return tradeBanExpiration;
    }

    public void setTradeBanExpiration(long tradeBanExpiration) {
        this.tradeBanExpiration = tradeBanExpiration;
    }

    public boolean isTradeBanned() {
        if (this.tradeBanExpiration > 0) {
            return this.tradeBanExpiration > DateUtil.getCurrentTimeSeconds();
        }

        return false;
    }

    public int getFavouriteGroupId() {
        return favouriteGroupId;
    }

    public void setFavouriteGroupId(int favouriteGroupId) {
        this.favouriteGroupId = favouriteGroupId;
    }


    public GroupMember getGroupMember() {
        Group group;

        if (this.groupMember == null) {
            if (this.getFavouriteGroupId() > 0) {
                var player = PlayerManager.getInstance().getPlayerById(this.id);

                if (player == null) {
                    group = GroupDao.getGroup(this.favouriteGroupId);
                }
                else {
                    group = player.getJoinedGroups().stream().filter(x -> x.getId() == this.favouriteGroupId).findFirst().orElse(null);
                }

                if (group == null) {
                    this.favouriteGroupId = 0;
                    PlayerDao.saveFavouriteGroup(this.id, 0);
                } else if (group.getOwnerId() == this.id) {
                    return new GroupMember(this.id, this.favouriteGroupId, false, 3);
                }

                this.groupMember = GroupMemberDao.getMember(this.favouriteGroupId, this.id);
            }
        }

        return this.groupMember;
    }

    public String getIpAddress() {
        return PlayerDao.getLatestIp(this.getId());
    }

    public boolean isReceiveNews() {
        return receiveNews;
    }

    public void setReceiveNews(boolean receiveNews) {
        this.receiveNews = receiveNews;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getConsoleMotto() {
        return consoleMotto;
    }

    public void setConsoleMotto(String consoleMotto) {
        this.consoleMotto = consoleMotto;
    }
}
