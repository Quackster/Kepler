package org.alexdev.kepler.game.challenges;

public class DailyTaskReward {
    private final int contentType;
    private final String itemTypeId;
    private final String parameter;
    private final int itemCount;

    public DailyTaskReward(int contentType, String itemTypeId, String parameter, int itemCount) {
        this.contentType = contentType;
        this.itemTypeId = itemTypeId;
        this.parameter = parameter;
        this.itemCount = itemCount;
    }

    public int getContentType() {
        return contentType;
    }

    public String getItemTypeId() {
        return itemTypeId;
    }

    public String getParameter() {
        return parameter;
    }

    public int getItemCount() {
        return itemCount;
    }
}
