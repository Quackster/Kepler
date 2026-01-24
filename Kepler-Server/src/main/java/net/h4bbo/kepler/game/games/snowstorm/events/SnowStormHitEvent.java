package net.h4bbo.kepler.game.games.snowstorm.events;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class SnowStormHitEvent extends GameObject {
    private final int throwerId;
    private final int targetId;
    private final int hitDirection;

    public SnowStormHitEvent(int throwerId, int targetId, int hitDirection) {
        super(-1, GameObjectType.SNOWSTORM_HIT_EVENT);
        this.throwerId = throwerId;
        this.targetId = targetId;
        this.hitDirection = hitDirection;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(this.getGameObjectType().getObjectId());
        response.writeInt(this.throwerId);
        response.writeInt(this.targetId);
        response.writeInt(this.hitDirection);
    }
}
