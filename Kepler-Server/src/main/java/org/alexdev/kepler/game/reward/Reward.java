package org.alexdev.kepler.game.reward;

import java.util.Date;

public class Reward {
    private int id;
    private String description;
    private Date availableFrom;
    private Date availableTo;
    private String itemDefinitions;

    public Reward(int id, String description, Date availableFrom, Date availableTo, String itemDefinitions) {
        this.id = id;
        this.description = description;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.itemDefinitions = itemDefinitions;
    }

    public int getId() {
        return id;
    }

    public String getItemDefinitions() {
        return itemDefinitions;
    }

    public Date getAvailableTo() {
        return availableTo;
    }

    public Date getAvailableFrom() {
        return availableFrom;
    }

    public String getDescription() {
        return description;
    }
}
