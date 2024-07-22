package org.alexdev.kepler.game.moderation;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.commandqueue.CommandQueue;
import org.alexdev.kepler.game.commandqueue.CommandQueueManager;
import org.alexdev.kepler.game.commandqueue.CommandType;
import org.alexdev.kepler.game.commands.CommandManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.server.rabbitmq.HabboActivityQueueSingleton;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.util.config.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ChatManager {
    private static ChatManager instance;
    private BlockingQueue<ChatMessage> chatMessageQueue;
    private static final Logger log = LoggerFactory.getLogger(CommandManager.class);

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
        this.performChatQueuePublish(player, room, message, chatMessageType);
    }

    /**
     * Save all the chat messages.
     */
    public void performChatSaving() {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        this.chatMessageQueue.drainTo(chatMessageList);
        RoomDao.saveChatLog(chatMessageList);
    }

    public void performChatQueuePublish(Player player, Room room, String message, CHAT_MESSAGE.ChatMessageType chatMessageType) {
        try {
            // Using gson to convert the object to a json string
            String messageJson = new Gson().toJson(new ChatMessage(player.getDetails().getId(), message, chatMessageType, room.getId(), DateUtil.getCurrentTimeSeconds()));
            HabboActivityQueueSingleton.getInstance().publishMessage("chat", messageJson);
        } catch(Exception e) {
            log.error("Failed to setup RabbitMQ", e);
        }
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
