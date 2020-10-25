package org.alexdev.kepler.game.player;

import org.alexdev.kepler.dao.mysql.BadgeDao;
import org.alexdev.kepler.dao.mysql.BanDao;
import org.alexdev.kepler.dao.mysql.GroupDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.ban.BanType;
import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.StringUtil;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PlayerDetails {
    // Basic info
    private int id;
    private String username;
    private String figure;
    private String poolFigure;
    private int credits;
    private int group;
    private int groupStatus;
    private String motto;
    private String consoleMotto;
    private char sex;

    // Currencies
    private int tickets;
    private int film;
    private PlayerRank rank;

    // Club
    private long firstClubSubscription;
    private long clubExpiration;
    private long clubGiftDue;

    // Badges
    private String currentBadge;
    private boolean showBadge;
    private List<String> badges;

    // Settings
    private boolean allowStalking;
    private boolean allowFriendRequests;
    private boolean soundEnabled;
    private boolean tutorialFinished;

    // Timestamps
    private long nextHandout;
    private long lastOnline;

    // Game points
    private int snowstormPoints;
    private int battleballPoints;

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
     * @param consoleMotto the console motto
     * @param sex the sex
     * @param tickets the tickets
     * @param film the film
     * @param rank the rank
     * @param lastOnline the last time they were online in a unix timestamp
     * @param firstClubSubscription the club subscribed date in a unix timestamp
     * @param clubExpiration the club expiration date in a unix timestamp
     * @param currentBadge the current badge
     * @param showBadge whether the badge is shown or not
     * @param allowStalking allow stalking/following
     * @param soundEnabled allow playing sound from client
     * @param battleballPoints the points accumulated when playing battleball
     * @param snowstormPoints the points accumulated when playing snowstorm
     * @param group group
     */
    public void fill(int id, String username, String figure, String poolFigure, int credits, String motto, String consoleMotto, String sex, int tickets, int film, int rank, long lastOnline, long firstClubSubscription, long clubExpiration, long clubGiftDue, String currentBadge, boolean showBadge, boolean allowStalking, boolean allowFriendRequests, boolean soundEnabled,
                     boolean tutorialFinished, int battleballPoints, int snowstormPoints, int group) {
        this.id = id;
        this.username = StringUtil.filterInput(username, true);
        this.figure = StringUtil.filterInput(figure, true); // Format: hd-180-1.ch-255-70.lg-285-77.sh-295-74.fa-1205-91.hr-125-31.ha-1016-
        this.poolFigure = StringUtil.filterInput(poolFigure, true); // Format: ch=s02/238,238,238
        this.motto = StringUtil.filterInput(motto, true);
        this.consoleMotto = StringUtil.filterInput(consoleMotto, true);
        this.sex = sex.toLowerCase().equals("f") ? 'F' : 'M';
        this.credits = credits;
        this.tickets = tickets;
        this.film = film;
        this.rank = PlayerRank.getRankForId(rank);
        this.lastOnline = lastOnline;
        this.firstClubSubscription = firstClubSubscription;
        this.clubExpiration = clubExpiration;
        this.clubGiftDue = clubGiftDue;
        this.currentBadge = currentBadge;
        this.showBadge = showBadge;
        this.allowStalking = allowStalking;
        this.allowFriendRequests = allowFriendRequests;
        this.soundEnabled = soundEnabled;
        this.tutorialFinished = tutorialFinished;
        this.battleballPoints = battleballPoints;
        this.snowstormPoints = snowstormPoints;
        this.group = group;
        if(group > 0) {
            this.groupStatus = GroupDao.getMemberStatus(group, id);
        } else {
            this.groupStatus = 0;
        }

        if (this.credits < 0) {
            // TODO: log warning
            this.credits = 0;
        }

        if (this.tickets < 0) {
            // TODO: log warning
            this.tickets = 0;
        }

        if (this.film < 0) {
            // TODO: log warning
            this.film = 0;
        }
    }

    public void fill(int id, String username, String figure, String motto, String sex) {
        this.id = id;
        this.username = username;
        this.figure = figure;
        this.poolFigure = "";
        this.motto = motto;
        this.sex = sex.toLowerCase().equals("f") ? 'F' : 'M';
    }

    public void loadBadges() {
        this.badges = BadgeDao.getBadges(this.id);
    }

    public boolean hasClubSubscription() {
        if (this.clubExpiration > 0) {
            if (DateUtil.getCurrentTimeSeconds() < this.clubExpiration) {
                return true;
            }
        }

        return false;
    }

    public boolean hasGoldClubSubscription() {
        if (this.hasClubSubscription()) {
            int sinceMonths = (int) (DateUtil.getCurrentTimeSeconds() - this.firstClubSubscription) / 60 / 60 / 24 / 31;

            // We are deemed a 'Gold' Club member if the user has been a club subscriber for a year
            // According to the HabboX wiki the badge is to be received on the first day of the 13th subscribed month
            return this.hasClubSubscription() && sinceMonths > 12;
        }

        return false;
    }

    public Pair<String, Long> isBanned() {
        var userBanCheck = BanDao.hasBan(BanType.USER_ID, this.id);

        if (userBanCheck != null) {
            return userBanCheck;
        }


        var ipBanCheck = BanDao.hasBan(BanType.IP_ADDRESS, PlayerDao.getLatestIp(this.id));

        if (ipBanCheck != null) {
            return ipBanCheck;
        }

        return null;
    }

    public int getId() {
        return id;
    }

    public int getGroup() {
        return group;
    }
    public int getGroupStatus() {
        return groupStatus;
    }

    public void setGroup(int group) {
        this.group = group;
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

    public String getConsoleMotto() {
        return consoleMotto;
    }

    public void setConsoleMotto(String consoleMotto) {
        this.consoleMotto = consoleMotto;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
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

    public String getCurrentBadge() {
        return currentBadge;
    }

    public void setCurrentBadge(String badge) {
        this.currentBadge = badge;
    }

    public boolean getShowBadge() {
        return showBadge;
    }

    public void setShowBadge(boolean badgeActive) {
        this.showBadge = badgeActive;
    }

    public List<String> getBadges() {
        return this.badges;
    }

    public void setBadges(List<String> badges) {
        this.badges = badges;
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

    public void resetNextHandout() {
        TimeUnit unit = TimeUnit.valueOf(GameConfiguration.getInstance().getString("credits.scheduler.timeunit"));
        this.nextHandout = DateUtil.getCurrentTimeSeconds()
                + unit.toSeconds(GameConfiguration.getInstance().getInteger("credits.scheduler.interval"));
    }

    public boolean isTutorialFinished() {
        return tutorialFinished;
    }

    public void setTutorialFinished(boolean tutorialFinished) {
        this.tutorialFinished = tutorialFinished;
    }

    public int getSnowStormPoints() {
        return snowstormPoints;
    }

    public void setSnowStormPoints(int snowstormPoints) {
        this.snowstormPoints = snowstormPoints;
    }

    public int getBattleballPoints() {
        return battleballPoints;
    }

    public void setBattleballPoints(int battleballPoints) {
        this.battleballPoints = battleballPoints;
    }

    public int getGamePoints(GameType type) {
        if (type == GameType.BATTLEBALL) {
            return this.battleballPoints;
        }

        if (type == GameType.SNOWSTORM) {
            return this.snowstormPoints;
        }

        return -1;
    }

    public boolean isAllowFriendRequests() {
        return allowFriendRequests;
    }

    public long getClubGiftDue() {
        return clubGiftDue;
    }

    public void setClubGiftDue(long clubGiftDue) {
        this.clubGiftDue = clubGiftDue;
    }
}
