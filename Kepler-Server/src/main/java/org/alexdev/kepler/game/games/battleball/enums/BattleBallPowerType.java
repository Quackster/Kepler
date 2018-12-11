package org.alexdev.kepler.game.games.battleball.enums;

import java.util.concurrent.ThreadLocalRandom;

public enum BattleBallPowerType {
    LIGHT_BLUB(1),
    SPRING(2),
    FLASHLIGHT(3),
    CANNON(4),
    BOX_OF_PINS(5),
    HARLEQUIN(6),
    BOMB(7),
    DRILL(8),
    QUESTION_MARK(9);

    private final int powerUpId;

    BattleBallPowerType(int powerUpId) {
        this.powerUpId = powerUpId;
    }

    public static BattleBallPowerType random() {
        return BattleBallPowerType.values()[ThreadLocalRandom.current().nextInt(0, BattleBallPowerType.values().length)];
    }

    public static BattleBallPowerType getById(int powerUpId) {
        for (var powerUp : values()) {
            if (powerUp.getPowerUpId() == powerUpId) {
                return powerUp;
            }
        }

        return null;
    }

    public Integer getPowerUpId() {
        return powerUpId;
    }
}
