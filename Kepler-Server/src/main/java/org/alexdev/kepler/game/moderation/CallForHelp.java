package org.alexdev.kepler.game.moderation;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.RoomManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class CallForHelp {

    private final int id;
    private final int callerId;
    private final String message;
    private int pickedUpBy;
    private final int roomId;
    private final long requestTime;

    // TODO: enumize category
    private int category = 2;

    CallForHelp(int id, int callerId, int roomId, String message) {
        this.id = id;
        this.callerId = callerId;
        this.message = message;
        this.pickedUpBy = 0;
        this.roomId = roomId;
        this.requestTime = System.currentTimeMillis();
    }

    public String getMessage() {
        return this.message;
    }

    public int getPickedUpBy() {
        return this.pickedUpBy;
    }

    public int getRoomId() {
        return this.roomId;
    }

    public int getCategory() {
        return this.category;
    }

    public int getCaller() {
        return this.callerId;
    }

    public int getCallId() {
        return this.id;
    }

    public boolean isOpen() {
        return this.pickedUpBy == 0;
    }

    public String getRoomName() {
        return RoomManager.getInstance().getRoomById(this.roomId).getData().getPublicName();
    }

    public String getRoomOwner() {
        return RoomManager.getInstance().getRoomById(this.roomId).getData().getOwnerName();
    }

    public String getFormattedRequestTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm d/MM/YYYY");
        Date resultDate = new Date(this.requestTime);
        return sdf.format(resultDate);
    }

    public void updateCategory(int newCategory) {
        this.category = newCategory;
    }

    public void setPickedUpBy(Player moderator) {
        this.pickedUpBy = moderator.getDetails().getId();
    }
}
