package org.alexdev.kepler.game.ads;

public class Advertisement {
    private int id;
    private int roomId;
    private boolean isLoadingAd;
    private String image;
    private String url;
    private boolean enabled;

    public Advertisement(int id, boolean isLoadingAd, int roomId, String image, String url, boolean enabled) {
        this.id = id;
        this.isLoadingAd = isLoadingAd;
        this.roomId = roomId;
        this.image = image;
        this.url = url;
        this.enabled = enabled;
    }

    public int getId() {
        return id;
    }

    public boolean isLoadingAd() {
        return isLoadingAd;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public boolean isEnabled() {
        return enabled;
    }
}