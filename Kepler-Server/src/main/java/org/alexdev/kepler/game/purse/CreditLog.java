package org.alexdev.kepler.game.purse;

import java.util.ArrayList;
import java.util.List;

public class CreditLog {
    private long timestamp;
    private int credits;
    private String type;
    private int userId;

    public CreditLog(int userId, long timestamp, String type, int credits) {
        this.credits = credits;
        this.userId = userId;
        this.timestamp = timestamp;
        this.type = type;
    }
    
    public int getCredits() {
        return credits;
    }
    public int getUserId() {
        return userId;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public String getType() {
        return type;
    }

}
