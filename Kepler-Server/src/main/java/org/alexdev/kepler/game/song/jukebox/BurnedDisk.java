package org.alexdev.kepler.game.song.jukebox;

public class BurnedDisk {
    private long itemId;
    private int soundmachineId;
    private int slotId;
    private int songId;
    private long burned;

    public BurnedDisk(long itemId, int soundmachineId, int slotId, int songId, long burned) {
        this.itemId = itemId;
        this.soundmachineId = soundmachineId;
        this.slotId = slotId;
        this.songId = songId;
        this.burned = burned;
    }

    public long getItemId() {
        return itemId;
    }

    public int getSoundmachineId() {
        return soundmachineId;
    }

    public int getSlotId() {
        return slotId;
    }

    public int getSongId() {
        return songId;
    }

    public long getBurned() {
        return burned;
    }
}
