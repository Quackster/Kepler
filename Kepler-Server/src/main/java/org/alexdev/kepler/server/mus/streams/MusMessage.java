package org.alexdev.kepler.server.mus.streams;

public class MusMessage {
    private int size;
    private int errorCode;
    private long timestamp;
    private String subject;
    private String senderId;
    private String[] receivers;
    private short contentType;
    private int contentInt;
    private String contentString;
    private MusPropList contentPropList;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

    public short getContentType() {
        return contentType;
    }

    public void setContentType(short contentType) {
        this.contentType = contentType;
    }

    public int getContentInt() {
        return contentInt;
    }

    public void setContentInt(int contentInt) {
        this.contentInt = contentInt;
    }

    public String getContentString() {
        return contentString;
    }

    public void setContentString(String contentString) {
        this.contentString = contentString;
    }

    public MusPropList getContentPropList() {
        return contentPropList;
    }

    public void setContentPropList(MusPropList contentPropList) {
        this.contentPropList = contentPropList;
    }

    @Override
    public String toString() {
        if (this.contentString != null)
            return this.subject + ":\"" + this.contentString + "\"";
        else
            return this.subject + ":\"\"";
    }
}
