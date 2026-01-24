package net.h4bbo.kepler.game.games.gamehalls.utils;

public enum GameShipType {
    AIRCRAFT_CARRIER(5, 1, 5),
    BATTLESHIP(4, 2, 4),
    CRUISER(3, 3, 3),
    DESTROYER(2, 4, 2);

    private final int id;
    private final int maxAllowed;
    private final int length;

    GameShipType(int id, int maxAllowed, int length) {
        this.id = id;
        this.maxAllowed = maxAllowed;
        this.length = length;
    }

    public static GameShipType getById(int id) {
        for (GameShipType shipType : values()) {
            if (shipType.getId() == id) {
                return shipType;
            }
        }

        return null;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return length;
    }
}
