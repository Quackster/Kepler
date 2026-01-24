package net.h4bbo.kepler.game.games.snowstorm.events;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class SnowStormThrowEvent extends GameObject {
    private final int x;
    private final int y;
    private final int throwHeight;

    public SnowStormThrowEvent(int objectId, int x, int y, int throwHeight) {
        super(objectId, GameObjectType.SNOWWAR_TARGET_THROW_EVENT);
        this.x = x;
        this.y = y;
        this.throwHeight = throwHeight;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(this.getGameObjectType().getObjectId());
        response.writeInt(getId());
        response.writeInt(this.x);
        response.writeInt(this.y);
        response.writeInt(this.throwHeight);
    }
}
