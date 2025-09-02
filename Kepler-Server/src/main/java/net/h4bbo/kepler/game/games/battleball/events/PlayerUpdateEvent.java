package net.h4bbo.kepler.game.games.battleball.events;

import net.h4bbo.kepler.game.games.GameEvent;
import net.h4bbo.kepler.game.games.battleball.objects.PlayerUpdateObject;
import net.h4bbo.kepler.game.games.enums.GameEventType;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class PlayerUpdateEvent extends GameEvent {
    private final GamePlayer gamePlayer;

    public PlayerUpdateEvent(GamePlayer gamePlayer) {
        super(GameEventType.BATTLEBALL_OBJECT_SPAWN);
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void serialiseEvent(NettyResponse response) {
        response.writeInt(0);
        new PlayerUpdateObject(this.gamePlayer).serialiseObject(response);
    }
}
