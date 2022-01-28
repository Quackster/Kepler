package org.alexdev.kepler.game.group;

public class Group {
    private int id;
    private String badge;
    private String name;
    private String description;

    public Group(int id, String badge, String name, String description) {
        this.id = id;
        this.badge = badge;
        this.name = name;
        this.description = description;
    }

    public int getId() { return this.id; }
    public String getBadge() { return this.badge; }
    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
}