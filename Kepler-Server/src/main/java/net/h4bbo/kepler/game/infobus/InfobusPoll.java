package net.h4bbo.kepler.game.infobus;

import net.h4bbo.kepler.dao.mysql.PlayerDao;
import net.h4bbo.kepler.util.DateUtil;

public class InfobusPoll {
    private int id;
    private int initiatedBy;
    private InfobusPollData infobusPollData;
    private long createdAt;

    public InfobusPoll(int id, int initiatedBy, InfobusPollData infobusPollData, long createdAt) {
        this.id = id;
        this.initiatedBy = initiatedBy;
        this.infobusPollData = infobusPollData;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getInitiatedBy() {
        return initiatedBy;
    }

    public String getCreator() {
        return PlayerDao.getName(this.initiatedBy);
    }

    public InfobusPollData getPollData() {
        return infobusPollData;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtFormatted() {
        return DateUtil.getDate(createdAt, DateUtil.LONG_DATE);
    }
}
