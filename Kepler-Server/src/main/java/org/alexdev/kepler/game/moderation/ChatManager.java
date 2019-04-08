package org.alexdev.kepler.game.moderation;

import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatManager {
    private static ChatManager instance;
    private BlockingQueue<ChatMessage> chatMessageQueue;

    public ChatManager() {
        this.chatMessageQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Queue messages to be saved to the database.
     *
     * @param player the player who sent the message
     * @param message the message
     * @param chatMessageType the message type
     * @param room the room the message was sent in
     */
    public void queue(Player player, Room room, String message, CHAT_MESSAGE.ChatMessageType chatMessageType) {
        this.chatMessageQueue.add(new ChatMessage(player.getDetails().getId(), message, chatMessageType, room.getId(), DateUtil.getCurrentTimeSeconds()));
    }

    /**
     * Save all the chat messages.
     */
    public void performChatSaving() {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        this.chatMessageQueue.drainTo(chatMessageList);
        RoomDao.saveChatLog(chatMessageList);
    }

    /**
     * Get the {@link ChatManager} instance
     *
     * @return the item manager instance
     */
    public static ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }

        return instance;
    }

}
