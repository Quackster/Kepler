package net.h4bbo.kepler.game.games.snowstorm.messages;

import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.games.snowstorm.messages.incoming.SnowStormAttackPlayerMessage;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormEvent;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormMessage;
import net.h4bbo.kepler.game.games.snowstorm.messages.incoming.SnowStormThrowLocationMessage;
import net.h4bbo.kepler.game.games.snowstorm.messages.incoming.SnowStormWalkMessage;
import net.h4bbo.kepler.game.games.snowstorm.messages.incoming.SnowstormCreateSnowballMessage;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.EnumMap;

public class SnowStormMessageHandler {
    private static SnowStormMessageHandler instance;
    private final EnumMap<SnowStormEvent, SnowStormMessage> events;

    public SnowStormMessageHandler() {
        this.events = new EnumMap<>(SnowStormEvent.class);
        this.events.put(SnowStormEvent.WALK, new SnowStormWalkMessage());
        this.events.put(SnowStormEvent.CREATE_SNOWBALL, new SnowstormCreateSnowballMessage());
        this.events.put(SnowStormEvent.THROW_SNOWBALL_AT_LOCATION, new SnowStormThrowLocationMessage());
        this.events.put(SnowStormEvent.THROW_SNOWBALL_AT_PERSON, new SnowStormAttackPlayerMessage());
    }

    public void handleMessage(int messageId, NettyRequest request, SnowStormGame snowStormGame, GamePlayer player) {
        var event = SnowStormEvent.getEvent(messageId);
        var handler = event != null ? events.get(event) : null;

        if (handler != null) {
            handler.handle(request, snowStormGame, player);
        }
        else {
            System.out.println("Unknown snowstorm event: " + messageId);
        }
    }

    public static SnowStormMessageHandler getInstance() {
        if (instance == null) {
            instance = new SnowStormMessageHandler();
        }

        return instance;
    }
}
