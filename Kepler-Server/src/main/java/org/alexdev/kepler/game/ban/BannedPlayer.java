package org.alexdev.kepler.game.ban;

import org.alexdev.kepler.game.ban.BanType;

public class BannedPlayer {
    private int id;
    private String reason;
    private int userId;
    private String ip;
    private BanType banType;
    private Long bannedUntil;

    public BannedPlayer(int id, String reason, String ip, int userId, BanType banType, Long bannedUntil) {
        this.id = id;
        this.banType = banType;
        this.userId = userId;
        this.ip = ip;
        this.reason = reason;
        this.bannedUntil = bannedUntil;
    }
    public int getId() {
        return this.id;
    }

    public String getReason() {
        return this.reason;
    }

    public String getIp() {
        return this.ip;
    }

    public Long getBannedUntil() {
        return this.bannedUntil;
    }

    public BanType getBanType() {
        return this.banType;
    }

    public int getUserId() {
        return this.userId;
    }

}
