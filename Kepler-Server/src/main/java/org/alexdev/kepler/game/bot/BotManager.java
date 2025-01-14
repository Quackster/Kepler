package org.alexdev.kepler.game.bot;

import org.alexdev.kepler.dao.mysql.BotDao;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.tasks.BotTask;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BotManager {
    private static BotManager instance;

    public BotManager() {

    }

    /**
     * Called upon initial room entry to load room bots.
     *
     * @param room the room to add the bots to
     */
    public void addBots(Room room) {
        if (!GameConfiguration.getInstance().getBoolean("room.bots.enabled")) {
            return;
        }

        List<BotData> botDataList = BotDao.getBotData(room.getId());

        for (BotData botData : botDataList) {
            Bot bot = new Bot(botData);
            bot.getDetails().fill(botData.getId(), botData.getName(), botData.getFigure(), botData.getMission(), "M");

            Position startPosition = botData.getStartPosition();
            startPosition.setZ(room.getMapping().getTile(botData.getStartPosition().getX(), botData.getStartPosition().getY()).getWalkingHeight());

            room.getEntityManager().enterRoom(bot, startPosition);
        }

        if (botDataList.size() > 0) {
            room.getTaskManager().scheduleTask("BotCommandTask", new BotTask(room), 0, 1, TimeUnit.SECONDS);
        }
    }


    public void removeBots(Room room) {
        List<Bot> bots = new ArrayList<>(room.getEntityManager().getEntitiesByClass(Bot.class));

        for (Bot bot : bots) {
            room.getEntityManager().leaveRoom(bot, false);
        }

        room.getTaskManager().cancelTask("BotCommandTask");

    }

    public void setBotStatus(String botName, Room room, StatusType statusType, Object value) {
        for (Bot bot : room.getEntityManager().getEntitiesByClass(Bot.class)) {
            if (bot.getDetails().getName().contains(botName)) {
                bot.getRoomUser().setStatus(statusType, value);
            }
        }
    }

    public void setBotStatus(Room room, StatusType statusType, Object value) {
        for (Bot bot : room.getEntityManager().getEntitiesByClass(Bot.class)) {
            bot.getRoomUser().setStatus(statusType, value);
        }
    }

    public void removeBotStatus(Room room, StatusType statusType) {
        for (Bot bot : room.getEntityManager().getEntitiesByClass(Bot.class)) {
            bot.getRoomUser().removeStatus(statusType);
        }
    }

    public void removeBotStatus(String botName, Room room, StatusType statusType) {
        for (Bot bot : room.getEntityManager().getEntitiesByClass(Bot.class)) {
            if (bot.getDetails().getName().contains(botName)) {
                bot.getRoomUser().removeStatus(statusType);
            }
        }
    }

    public void resetBots(Room room) {
        this.removeBots(room);
        this.addBots(room);
    }

    public void talk(int roomId, int botId, String message, CHAT_MESSAGE.ChatMessageType type) {
        Room room = RoomManager.getInstance().getRoomById(roomId);

        if (room == null) {
            return;
        }

        for (Bot bot : room.getEntityManager().getEntitiesByClass(Bot.class)) {
            if (bot.getDetails().getId() == botId) {
                bot.getRoomUser().talk(message, type);
            }
        }
    }

    /**
     * Handle the speech called upon the bots.
     *
     * @param player the player speaking
     * @param room the room speaking in
     * @param message the message spoken
     */
    public void handleSpeech(Player player, Room room, String message) {
        List<Bot> bots = new ArrayList<>();

        for (Bot bot : room.getEntityManager().getEntitiesByClass(Bot.class)) {
            if (bot.getRoomUser().getPosition().getDistanceSquared(player.getRoomUser().getPosition()) > 14) {
                continue;
            }

            if (bot.getBotData() == null) {
                continue;
            }

            bots.add(bot);
        }

        for (Bot bot : bots) {
            String drink = HasRequestedDrink(player, bot, message);

            if (drink != null) {
                if (bot.getBotData().getResponses().size() > 0) {
                    var botSpeech = bot.getBotData().getResponses().get(ThreadLocalRandom.current().nextInt(bot.getBotData().getResponses().size()));
                    var chatMessage = botSpeech.getSpeech();

                    chatMessage = chatMessage.replace("%lowercaseDrink%", drink.toLowerCase());
                    chatMessage = chatMessage.replace("%drink%", drink);

                    bot.getRoomUser().talk(chatMessage, botSpeech.getChatMessageType());
                }
                continue;
            }

            if (message.toLowerCase().contains(bot.getDetails().getName().toLowerCase())) {
                if (bot.getBotData().getUnrecognisedSpeech().size()>0) {
                    var botSpeech = bot.getBotData().getUnrecognisedSpeech().get(ThreadLocalRandom.current().nextInt(bot.getBotData().getUnrecognisedSpeech().size()));
                    bot.getRoomUser().talk(botSpeech.getSpeech(), botSpeech.getChatMessageType());
                }
            }
        }
    }

    private String HasRequestedDrink(Player player, Bot bot, String message) {
        if (bot.getBotData().getDrinks().isEmpty())
            return null;

        for (String drink : bot.getBotData().getDrinks()) {
            if (message.toLowerCase().contains(drink.toLowerCase())) {
                player.getRoomUser().carryItem(-1, drink);
                return drink;
            }
        }

        if (message.toLowerCase().contains("drink please") ||
                message.toLowerCase().contains("can i have") ||
                message.toLowerCase().contains("i'll have")) {
            String drink = bot.getBotData().getDrinks().get(ThreadLocalRandom.current().nextInt(bot.getBotData().getDrinks().size()));
            player.getRoomUser().carryItem(-1, drink);
            return drink;
        }

        return null;
    }

    /**
     * Get the {@link BotManager} instance
     *
     * @return the catalogue manager instance
     */
    public static BotManager getInstance() {
        if (instance == null) {
            instance = new BotManager();
        }

        return instance;
    }
}
