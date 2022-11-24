package org.alexdev.kepler.game.reward;

public class Reward {
    private int id;
    private String description;
    private long availableFrom;
    private long availableTo;
    private String saleCodes;

    public Reward(int id, String description, long availableFrom, long availableTo, String saleCodes) {
        this.id = id;
        this.description = description;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.saleCodes = saleCodes;
    }

    public int getId() {
        return id;
    }

    public String getSaleCodes() {
        return saleCodes;
    }

    public long getAvailableTo() {
        return availableTo;
    }

    public long getAvailableFrom() {
        return availableFrom;
    }

    public String getDescription() {
        return description;
    }
}
