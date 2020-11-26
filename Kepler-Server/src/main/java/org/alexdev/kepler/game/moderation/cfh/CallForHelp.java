package org.alexdev.kepler.game.moderation.cfh;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class CallForHelp {
    private final int cryId;
    private final int callerId;
    private final String message;
    private String pickedUpBy;
    private final Room room;
    private final long requestTime;
    private int category = 2;
    private long expireTime;

    CallForHelp(int cryId, int callerId, Room room, String message) {
        this.cryId = cryId;
        this.callerId = callerId;
        this.message = message;
        this.pickedUpBy = "N/A";
        this.room = room;
        this.requestTime = System.currentTimeMillis();
    }

    public String getMessage() {
        return this.message;
    }

    public String getPickedUpBy() {
        return this.pickedUpBy;
    }

    public Room getRoom() {
        return this.room;
    }

    public int getCategory() {
        return this.category;
    }

    public int getCaller() {
        return this.callerId;
    }

    public int getCryId() {
        return this.cryId;
    }

    public boolean isOpen() {
        return this.pickedUpBy == "N/A";
    }

    public String getFormattedRequestTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/YYYY");
        Date resultDate = new Date(this.requestTime);
        return sdf.format(resultDate);
    }

    public void updateCategory(int newCategory) {
        this.category = newCategory;
    }

    public void setPickedUpBy(Player moderator) {
        this.pickedUpBy = moderator.getDetails().getName();
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
