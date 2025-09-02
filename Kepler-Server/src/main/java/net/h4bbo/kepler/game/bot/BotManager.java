package net.h4bbo.kepler.game.bot;

import net.h4bbo.kepler.dao.mysql.BotDao;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.tasks.BotTask;
import net.h4bbo.kepler.util.config.GameConfiguration;

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
            bot.getDetails().fill(0, botData.getName(), botData.getFigure(), botData.getMission(), "M");

            Position startPosition = botData.getStartPosition();
            startPosition.setZ(room.getMapping().getTile(botData.getStartPosition().getX(), botData.getStartPosition().getY()).getWalkingHeight());

            room.getEntityManager().enterRoom(bot, startPosition);
        }

        if (botDataList.size() > 0) {
            room.getTaskManager().scheduleTask("BotCommandTask", new BotTask(room), 0, 1, TimeUnit.SECONDS);
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
