package net.h4bbo.kepler.game.pets;

public enum PetAction {
    TALK(0),
    SIT(0),
    LAY(0),
    PLAY(0),
    WALK(0);

    private final int actionLength;

    PetAction(int actionLength) {
        this.actionLength = actionLength;
    }

    public int getActionLength() {
        return actionLength;
    }
}
