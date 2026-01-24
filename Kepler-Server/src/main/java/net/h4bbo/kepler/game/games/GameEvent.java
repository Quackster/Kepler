package net.h4bbo.kepler.game.games;

import net.h4bbo.kepler.game.games.enums.GameEventType;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public abstract class GameEvent {
    private GameEventType gameEventType;

    public GameEvent(GameEventType gameEventType) {
        this.gameEventType = gameEventType;
    }

    public abstract void serialiseEvent(NettyResponse response);

    /**
     * Get the game event type for the update loop
     *
     * @return the event type
     */
    public GameEventType getGameEventType() {
        return gameEventType;
    }
}
