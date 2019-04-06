package org.alexdev.kepler.game.room.tasks;

import org.alexdev.kepler.game.bot.Bot;
import org.alexdev.kepler.game.bot.BotSpeech;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.util.DateUtil;

import java.util.concurrent.ThreadLocalRandom;

public class BotTask implements Runnable {
    private final Room room;

    private int MIN_WALK_TIME = 3;
    private int MAX_WALK_TIME = 10;

    private int MIN_SPEAK_TIME = 20;
    private int MAX_SPEAK_TIME = 50;
    private BotSpeech lastSpeech;

    public BotTask(Room room) {
        this.room = room;

        for (Bot bot : this.room.getEntityManager().getEntitiesByClass(Bot.class)) {
            bot.setNextWalkTime(DateUtil.getCurrentTimeSeconds() + ThreadLocalRandom.current().nextInt(MIN_WALK_TIME, MAX_WALK_TIME));
            bot.setNextSpeechTime(DateUtil.getCurrentTimeSeconds() + ThreadLocalRandom.current().nextInt(MIN_SPEAK_TIME, MAX_SPEAK_TIME));
        }
    }

    @Override
    public void run() {
        for (Bot bot : this.room.getEntityManager().getEntitiesByClass(Bot.class)) {
            if (DateUtil.getCurrentTimeSeconds() > bot.getNextWalkTime()) {
                if (bot.getBotData().getWalkspace().size() > 0) {
                    Position walkDestination = bot.getBotData().getWalkspace().get(ThreadLocalRandom.current().nextInt(0, bot.getBotData().getWalkspace().size()));
                    bot.getRoomUser().walkTo(walkDestination.getX(), walkDestination.getY());
                    bot.setNextWalkTime(DateUtil.getCurrentTimeSeconds() + ThreadLocalRandom.current().nextInt(MIN_WALK_TIME, MAX_WALK_TIME));
                }
            }

            if (DateUtil.getCurrentTimeSeconds() > bot.getNextSpeechTime()) {
                if (bot.getBotData().getSpeeches().size() > 0) {
                    BotSpeech speech = bot.getBotData().getSpeeches().get(ThreadLocalRandom.current().nextInt(0, bot.getBotData().getSpeeches().size()));
                    bot.setNextSpeechTime(DateUtil.getCurrentTimeSeconds() + ThreadLocalRandom.current().nextInt(MIN_SPEAK_TIME, MAX_SPEAK_TIME));

                    if (this.lastSpeech != speech) {
                        bot.getRoomUser().talk(speech.getSpeech(), speech.getChatMessageType());
                    }

                    this.lastSpeech = speech;
                }
            }
        }
    }
}
