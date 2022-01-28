package org.alexdev.kepler.game.messenger;

public class MessengerMessage {
    private int id;
    private int toId;
    private int fromId;
    private long timeSet;
    private  String message;
    private  String url;
    private  String link;

    public MessengerMessage(int id, int toId, int fromId, long timeSet, String message, String link, String url) {
        this.id = id;
        this.toId = toId;
        this.fromId = fromId;
        this.timeSet = timeSet;
        this.message = message;
        this.url = link;
        this.link = url;
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
    public String getLink() {
        return link;
    }
    public String getUrl() {
        return url;
    }
}
