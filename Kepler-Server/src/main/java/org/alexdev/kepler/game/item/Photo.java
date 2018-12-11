package org.alexdev.kepler.game.item;

public class Photo {
    private int id;
    private int checksum;
    private byte[] data;
    private long time;

    public Photo(int id, int checksum, byte[] data, long time) {
        this.id = id;
        this.checksum = checksum;
        this.data = data;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChecksum() {
        return checksum;
    }

    public byte[] getData() {
        return data;
    }

    public long getTime() {
        return time;
    }
}
