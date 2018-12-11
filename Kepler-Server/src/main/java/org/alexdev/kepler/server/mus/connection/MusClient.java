package org.alexdev.kepler.server.mus.connection;

import io.netty.channel.Channel;

public class MusClient {
    private Channel channel;
    private String photoText;
    private int userId;

    public MusClient(Channel channel) {
        this.channel = channel;
        this.userId = 0;
        this.photoText = "";
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhotoText() {
        return photoText;
    }

    public void setPhotoText(String photoText) {
        this.photoText = photoText;
    }

    public Channel getChannel() {
        return channel;
    }
}
