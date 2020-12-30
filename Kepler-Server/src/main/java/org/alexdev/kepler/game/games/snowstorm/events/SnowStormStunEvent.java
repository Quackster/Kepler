package org.alexdev.kepler.game.games.snowstorm.events;

import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class SnowStormStunEvent extends GameObject {
    private final int stunnedId;
    private final int throwerId;
    private final int hitDirection;

    public SnowStormStunEvent(int throwerId, int hitId, int hitDirection) {
        super(-1, GameObjectType.SNOWWAR_STUN_EVENT);
        this.stunnedId = throwerId;
        this.throwerId = hitId;
        this.hitDirection = hitDirection;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(this.getGameObjectType().getObjectId());
        response.writeInt(this.stunnedId);
        response.writeInt(this.throwerId);
        response.writeInt(this.hitDirection);

    }
}