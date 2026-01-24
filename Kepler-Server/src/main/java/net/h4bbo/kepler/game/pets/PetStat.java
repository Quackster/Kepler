package net.h4bbo.kepler.game.pets;

public enum PetStat {
    HUNGER(6),
    THIRST(3),
    HAPPINESS(6),
    ENERGY(7),
    FRIENDSHIP(7);

    private int attributeType;

    PetStat(int attributeType) {
        this.attributeType = attributeType;
    }

    public int getAttributeType() {
        return attributeType;
    }
}
