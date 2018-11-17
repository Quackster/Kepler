package org.alexdev.kepler.game.item.public_items;

public class PublicItemData {
    private String id;
    private String roomModel;
    private String sprite;
    private int x;
    private int y;
    private double z;
    private int rotation;
    private double topHeight;
    private int length;
    private int width;
    private String behaviour;
    private String currentProgram;

    public PublicItemData(String id, String roomModel, String sprite, int x, int y, double z, int rotation, double topHeight, int length, int width, String behaviour, String currentProgram) {
        this.id = id;
        this.roomModel = roomModel;
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.topHeight = topHeight;
        this.length = length;
        this.width = width;
        this.behaviour = behaviour;
        this.currentProgram = currentProgram;
    }

    public String getId() {
        return id;
    }

    public String getRoomModel() {
        return roomModel;
    }

    public String getSprite() {
        return sprite;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getRotation() {
        return rotation;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public String getCurrentProgram() {
        return currentProgram;
    }

    public double getTopHeight() {
        return topHeight;
    }
}
