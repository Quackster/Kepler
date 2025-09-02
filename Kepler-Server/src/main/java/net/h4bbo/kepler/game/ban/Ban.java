package net.h4bbo.kepler.game.ban;

import net.h4bbo.kepler.dao.mysql.BanDao;
import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.util.DateUtil;

public class Ban {
    private final BanType banType;
    private final String value;
    private final String message;
    private final long bannedUtil;
    private final long bannedAt;
    private final int bannedBy;

    public Ban(BanType banType, String value, String message, long bannedUntil, long bannedAt, int bannedBy) {
        this.banType = banType;
        this.value = value;
        this.message = message;
        this.bannedUtil = bannedUntil;
        this.bannedAt = bannedAt;
        this.bannedBy = bannedBy;
    }

    public String getName() {
        if (this.banType == BanType.MACHINE_ID) {
            return BanDao.getName(this.value);
        }

        if (this.banType == BanType.USER_ID) {
            var name = PlayerDao.getName(Integer.parseInt(this.value));
            return name != null ? name : "";
        }

        return "";
    }

    public String getBannedBy() {
        if (this.bannedBy == -1) {
            return "Triggered spam filter";
        }

        if (this.bannedBy > 0) {
            var name = PlayerDao.getName(this.bannedBy);
            return name != null ? name : "";
        }

        return "Legacy Banned";
    }

    public BanType getBanType() {
        return banType;
    }

    public String getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public String getBannedUtil() {
        return DateUtil.getDate(bannedUtil, DateUtil.LONG_DATE);
    }

    public String getBannedAt() {
        return DateUtil.getDate(bannedAt, DateUtil.LONG_DATE);
    }
}
