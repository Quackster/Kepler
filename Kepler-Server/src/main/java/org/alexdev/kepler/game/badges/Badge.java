package org.alexdev.kepler.game.badges;

public class Badge {
    private String badgeCode;
    private boolean equipped;
    private int slotId;

    public Badge(String badgeCode, boolean equipped, int slotId) {
        this.badgeCode = badgeCode;
        this.equipped = equipped;
        this.slotId = slotId;
    }

    public String getBadgeCode() {
        return badgeCode;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }
}
