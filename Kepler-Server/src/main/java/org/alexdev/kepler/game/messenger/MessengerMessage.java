package org.alexdev.kepler.game.messenger;

public class MessengerMessage {
    private int id;
    private int toId;
    private int fromId;
    private long timeSet;
    private  String message;

    public MessengerMessage(int id, int toId, int fromId, long timeSet, String message) {
        this.id = id;
        this.toId = toId;
        this.fromId = fromId;
        this.timeSet = timeSet;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public int getToId() {
        return toId;
    }

    public int getFromId() {
        return fromId;
    }

    public long getTimeSet() {
        return timeSet;
    }

    public String getMessage() {
        return message;
    }
}
