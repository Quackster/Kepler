package org.alexdev.kepler.game.pets;

import org.alexdev.kepler.game.player.PlayerDetails;

public class PetDetails extends PlayerDetails {
    private int id;
    private int itemId;
    private String name;
    private String type;
    private String race;
    private String colour;
    private int naturePositive;
    private int natureNegative;
    private float friendship;
    private long born;
    private long lastKip;
    private long lastEat;
    private long lastDrink;
    private long lastPlayToy;
    private long lastPlayUser;
    private int X;
    private int Y;
    private int Rotation;

    public PetDetails(int id, int itemId, String name, String type, String race, String colour, int naturePositive, int natureNegative, float friendship, long born, long lastKip, long lastEat, long lastDrink, long lastPlayToy, long lastPlayUser, int x, int y, int rotation) {
        this.id = id;
        this.itemId = itemId;
        this.name = name;
        this.type = type;
        this.race = race;
        this.colour = colour;
        this.naturePositive = naturePositive;
        this.natureNegative = natureNegative;
        this.friendship = friendship;
        this.born = born;
        this.lastKip = lastKip;
        this.lastEat = lastEat;
        this.lastDrink = lastDrink;
        this.lastPlayToy = lastPlayToy;
        this.lastPlayUser = lastPlayUser;
        this.X = x;
        this.Y = y;
        this.Rotation = rotation;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getItemId() {
        return itemId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFigure() {
        return this.type + " " + this.race + " " + this.colour;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public int getNaturePositive() {
        return naturePositive;
    }

    public void setNaturePositive(int naturePositive) {
        this.naturePositive = naturePositive;
    }

    public int getNatureNegative() {
        return natureNegative;
    }

    public void setNatureNegative(int natureNegative) {
        this.natureNegative = natureNegative;
    }

    public float getFriendship() {
        return friendship;
    }

    public void setFriendship(float friendship) {
        this.friendship = friendship;
    }

    public long getBorn() {
        return born;
    }

    public void setBorn(long born) {
        this.born = born;
    }

    public long getLastKip() {
        return lastKip;
    }

    public void setLastKip(long lastKip) {
        this.lastKip = lastKip;
    }

    public long getLastEat() {
        return lastEat;
    }

    public void setLastEat(long lastEat) {
        this.lastEat = lastEat;
    }

    public long getLastDrink() {
        return lastDrink;
    }

    public void setLastDrink(long lastDrink) {
        this.lastDrink = lastDrink;
    }

    public long getLastPlayToy() {
        return lastPlayToy;
    }

    public void setLastPlayToy(long lastPlayToy) {
        this.lastPlayToy = lastPlayToy;
    }

    public long getLastPlayUser() {
        return lastPlayUser;
    }

    public void setLastPlayUser(long lastPlayUser) {
        this.lastPlayUser = lastPlayUser;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getRotation() {
        return Rotation;
    }

    public void setRotation(int rotation) {
        Rotation = rotation;
    }
}
