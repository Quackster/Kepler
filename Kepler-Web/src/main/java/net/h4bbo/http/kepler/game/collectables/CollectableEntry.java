package net.h4bbo.http.kepler.game.collectables;

public class CollectableEntry {
    private String sprite;
    private String name;
    private String description;

    public CollectableEntry(String sprite, String name, String description) {
        this.sprite = sprite;
        this.name = name;
        this.description = description;
    }

    public String getSprite() {
        return sprite;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
