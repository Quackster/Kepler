package org.alexdev.kepler.game.games.snowstorm;

import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.objects.SnowStormSnowballObject;

/**
 * Represents a collision-related event that should occur a few ticks later.
 */
public class SnowStormDelayedEvent {
    private final SnowStormDelayedEventType eventType;
    private final GamePlayer player;
    private final SnowStormSnowballObject ball;

    public SnowStormDelayedEvent(SnowStormDelayedEventType eventType, GamePlayer player, SnowStormSnowballObject ball) {
        this.eventType = eventType;
        this.player = player;
        this.ball = ball;
    }

    public static SnowStormDelayedEvent hit(
            GamePlayer player,
            SnowStormSnowballObject ball) {

        return new SnowStormDelayedEvent(
                SnowStormDelayedEventType.HIT,
                player,
                ball
        );
    }

    public static SnowStormDelayedEvent stun(
            GamePlayer player,
            SnowStormSnowballObject ball) {

        return new SnowStormDelayedEvent(
                SnowStormDelayedEventType.STUN,
                player,
                ball
        );
    }

    public SnowStormDelayedEventType getEventType() {
        return eventType;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public SnowStormSnowballObject getBall() {
        return ball;
    }
}
