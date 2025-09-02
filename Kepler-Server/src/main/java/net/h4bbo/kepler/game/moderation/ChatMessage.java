package net.h4bbo.kepler.game.moderation;

import net.h4bbo.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;

public class ChatMessage {
    private final int playerId;
    private final String message;
    private final CHAT_MESSAGE.ChatMessageType chatMessageType;
    private final int roomId;
    private final long sentTime;

    public ChatMessage(int playerId, String message, CHAT_MESSAGE.ChatMessageType chatMessageType, int roomId, long sentTime) {
        this.playerId = playerId;
        this.roomId =  roomId;
        this.message = message;
        this.chatMessageType = chatMessageType;
        this.sentTime = sentTime;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getMessage() {
        return message;
    }

    public CHAT_MESSAGE.ChatMessageType getChatMessageType() {
        return chatMessageType;
    }

    public long getSentTime() {
        return sentTime;
    }
}
