package org.alexdev.kepler.game.messenger;

public class MessengerCategory {
    private int id;
    private int userId;
    private String name;

    public MessengerCategory(int id, int userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
