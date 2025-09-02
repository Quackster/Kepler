package net.h4bbo.http.kepler.game.minimail;

import net.h4bbo.kepler.game.player.PlayerDetails;
import net.h4bbo.kepler.game.wordfilter.WordfilterManager;
import net.h4bbo.kepler.util.DateUtil;
import net.h4bbo.http.kepler.util.BBCode;
import net.h4bbo.http.kepler.util.HtmlUtil;

public class MinimailMessage {
    private final int id;
    private boolean isRead;
    private int targetId;
    private int toId;
    private int senderId;
    private String subject;
    private String message;
    private final long dateSent;
    private int conversationId;
    private boolean isTrash;
    private PlayerDetails target;
    private PlayerDetails author;

    public MinimailMessage(int id, int targetId, int toId, int senderId, boolean isRead, String subject, String message, long dateSent, int conversationId, boolean isTrash) {
        this.id = id;
        this.targetId = targetId;
        this.toId = toId;
        this.isRead = isRead;
        this.senderId = senderId;
        this.subject = subject;
        this.message = message;
        this.dateSent = dateSent;
        this.conversationId = conversationId;
        this.isTrash = isTrash;
    }

    public int getId() {
        return id;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return WordfilterManager.filterSentence(message);
    }

    public String getFormattedSubject() {
        return BBCode.format(HtmlUtil.escape(this.subject), false);
    }

    public String getFormattedMessage() {
        return BBCode.format(HtmlUtil.escape(WordfilterManager.filterSentence(this.message)), false);
    }

    public long getDateSent() {
        return dateSent;
    }

    public String getDate() {
        return DateUtil.getFriendlyDate(this.dateSent);
    }

    public String getIsoDate() {
        return DateUtil.getDate(this.dateSent, "yyyy-MM-dd'T'HH:mm:ssZ");
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public boolean isTrash() {
        return isTrash;
    }

    public void setTrash(boolean trash) {
        isTrash = trash;
    }

    public PlayerDetails getTarget() {
        return target;
    }

    public void setTarget(PlayerDetails target) {
        this.target = target;
    }

    public PlayerDetails getAuthor() {
        return author;
    }

    public void setAuthor(PlayerDetails author) {
        this.author = author;
    }
}
