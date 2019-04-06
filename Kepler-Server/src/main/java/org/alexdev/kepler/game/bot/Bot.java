package org.alexdev.kepler.game.bot;

import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.room.entities.RoomBot;
import org.alexdev.kepler.game.room.entities.RoomEntity;

public class Bot extends Entity {
    private PlayerDetails playerDetails;
    private RoomBot roomUser;
    private BotData botData;
    private long nextWalkTime;
    private long nextSpeechTime;

    public Bot() {
        this.playerDetails = new PlayerDetails();
        this.roomUser = new RoomBot(this);
    }

    public Bot(BotData botData) {
        this.playerDetails = new PlayerDetails();
        this.roomUser = new RoomBot(this);
        this.botData = botData;
    }

    @Override
    public boolean hasFuse(Fuseright permission) {
        return false;
    }

    @Override
    public PlayerDetails getDetails() {
        return this.playerDetails;
    }

    @Override
    public RoomEntity getRoomUser() {
        return this.roomUser;
    }

    @Override
    public EntityType getType() {
        return EntityType.BOT;
    }

    @Override
    public void dispose() { }

    public BotData getBotData() {
        return botData;
    }

    public long getNextWalkTime() {
        return nextWalkTime;
    }

    public void setNextWalkTime(long nextWalkTime) {
        this.nextWalkTime = nextWalkTime;
    }

    public long getNextSpeechTime() {
        return nextSpeechTime;
    }

    public void setNextSpeechTime(long nextSpeechTime) {
        this.nextSpeechTime = nextSpeechTime;
    }
}
