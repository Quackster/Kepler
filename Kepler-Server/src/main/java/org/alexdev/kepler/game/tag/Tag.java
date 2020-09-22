package org.alexdev.kepler.game.tag;

public class Tag {
    private int id;
    private String tag;
    private int userId;

    public Tag(int id, String tag, int userId) {
        this.id = id;
        this.tag = tag;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }
    public String getTag() {
        return tag;
    }
}
