package org.alexdev.kepler.game.games.battleball.events;

import org.alexdev.kepler.game.games.GameEvent;
import org.alexdev.kepler.game.games.battleball.objects.PlayerUpdateObject;
import org.alexdev.kepler.game.games.enums.GameEventType;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

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
