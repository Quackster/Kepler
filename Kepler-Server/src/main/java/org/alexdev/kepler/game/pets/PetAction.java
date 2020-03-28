package org.alexdev.kepler.game.pets;

public enum PetAction {
    NONE(0),
    SLEEP(0),
    EAT(0),
    DRINK(0),
    WALKING(0),
    SIT(0),
    LAY(0),
    JUMP(0),
    DEAD(0);

    private final int actionLength;

    PetAction(int actionLength) {
        this.actionLength = actionLength;
    }

    public int getActionLength() {
        return actionLength;
    }
}
