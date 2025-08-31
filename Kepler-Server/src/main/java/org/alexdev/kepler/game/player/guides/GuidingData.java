package org.alexdev.kepler.game.player.guides;

public class GuidingData {
    private int userId;
    private String username;
    private long lastOnline;
    private long timeOnline;

    public GuidingData(int userId, String username, long lastOnline, long timeOnline) {
        this.userId = userId;
        this.username = username;
        this.lastOnline = lastOnline;
        this.timeOnline = timeOnline;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public long getTimeOnline() {
        return timeOnline;
    }
}
