package org.alexdev.kepler.game.bot;

import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;

import java.util.regex.Pattern;

public class BotSpeech {
    private String speech;
    private CHAT_MESSAGE.ChatMessageType chatMessageType;

    public BotSpeech(String speech) {
        if (speech.contains("#")) {
            this.speech = speech.split(Pattern.quote("#"))[0];
            this.chatMessageType = CHAT_MESSAGE.ChatMessageType.valueOf(speech.split(Pattern.quote("#"))[1]);
        } else {
            this.speech = speech;
            this.chatMessageType = CHAT_MESSAGE.ChatMessageType.CHAT;
        }
    }

    public String getSpeech() {
        return speech;
    }

    public CHAT_MESSAGE.ChatMessageType getChatMessageType() {
        return chatMessageType;
    }
}
