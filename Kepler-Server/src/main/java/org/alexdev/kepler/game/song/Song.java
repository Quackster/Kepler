package org.alexdev.kepler.game.song;

public class Song {
    private int id;
    private String title;
    private int itemId;
    private int userId;
    private int length;
    private String data;
    private boolean isBurnt;

    public Song() { }

    public Song(int id, String title, int itemId, int userId, int length, String data, boolean isBurnt) {
        this.id = id;
        this.title = title;
        this.itemId = itemId;
        this.userId = userId;
        this.length = length;
        this.data = data;
        this.isBurnt = isBurnt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isBurnt() {
        return isBurnt;
    }

    public void setBurnt(boolean burnt) {
        isBurnt = burnt;
    }
}
