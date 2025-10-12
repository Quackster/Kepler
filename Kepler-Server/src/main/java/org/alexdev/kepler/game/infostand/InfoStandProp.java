package org.alexdev.kepler.game.infostand;

import java.util.HashMap;

public enum InfoStandProp {
    PLATE(1),
    FURNI(2),
    EXPRESSION(3),
    ACTION(4),
    DIRECTION(5);

    private static final HashMap<Integer, InfoStandProp> map = new HashMap<>();
    private final int id;

    static {
        for (InfoStandProp prop : InfoStandProp.values()) {
            map.put(prop.getId(), prop);
        }
    }

    InfoStandProp(int id) {
        this.id = id;
    }

    public static InfoStandProp fromId(int id) {
        return map.get(id);
    }

    public int getId() {
        return id;
    }
}
