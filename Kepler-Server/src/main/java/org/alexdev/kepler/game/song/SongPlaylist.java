package org.alexdev.kepler.game.song;

public class SongPlaylist {
    private int itemId;
    private Song song;
    private int slotId;

    public SongPlaylist(int itemId, Song song, int slotId) {
        this.itemId = itemId;
        this.song = song;
        this.slotId = slotId;
    }

    public int getItemId() {
        return itemId;
    }

    public Song getSong() {
        return song;
    }

    public int getSlotId() {
        return slotId;
    }
}
