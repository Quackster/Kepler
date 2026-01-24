package net.h4bbo.http.kepler.game.housekeeping;

public class HousekeepingStats {
    private final int userCount;
    private final int inventoryItemsCount;
    private final int roomItemCount;
    private final int groupCount;
    private final int petCount;
    private final int photoCount;

    public HousekeepingStats(int userCount, int inventoryItemsCount, int roomItemCount, int groupCount, int petCount, int photoCount) {
        this.userCount = userCount;
        this.inventoryItemsCount = inventoryItemsCount;
        this.roomItemCount = roomItemCount;
        this.groupCount = groupCount;
        this.petCount = petCount;
        this.photoCount = photoCount;
    }

    public int getUserCount() {
        return userCount;
    }

    public int getInventoryItemsCount() {
        return inventoryItemsCount;
    }

    public int getRoomItemCount() {
        return roomItemCount;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public int getPetCount() {
        return petCount;
    }

    public int getPhotoCount() {
        return photoCount;
    }
}
