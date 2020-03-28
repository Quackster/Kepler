package org.alexdev.kepler.game.room.enums;

public enum StatusType {
    MOVE("mv"),
    SIT("sit"),
    LAY("lay"), 
    FLAT_CONTROL("flatctrl"), 
    DANCE("dance"),
    SWIM("swim"),
    CARRY_ITEM("cri"),
    CARRY_DRINK("carryd"),
    CARRY_FOOD("carryf"),
    USE_ITEM("usei"),
    USE_FOOD("eat"),
    USE_DRINK("drink"),
    WAVE("wave"),
    GESTURE("gest"),
    TALK("talk"),
    AVATAR_SLEEP("Sleep"),
    TRADE("trd"),
    SIGN("sign"),
    DEAD("ded"),
    JUMP("jmp"),
    PET_SLEEP("slp"),
    EAT("eat");

    private String statusCode;

    StatusType(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }
}
