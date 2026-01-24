package net.h4bbo.kepler.game.games.snowstorm.events;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class SnowStormLaunchSnowballEvent extends GameObject {
    private final int x;
    private final int y;
    private final int trajectory;
    private final int throwerId;

    public SnowStormLaunchSnowballEvent(int objectId, int throwerId, int x, int y, int trajectory) {
        super(objectId, GameObjectType.SNOWWAR_THROW_EVENT);
        this.throwerId = throwerId;
        this.x = x;
        this.y = y;
        this.trajectory = trajectory;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(this.getGameObjectType().getObjectId());
        response.writeInt(getId());
        response.writeInt(this.throwerId);
        response.writeInt(this.x);
        response.writeInt(this.y);
        response.writeInt(this.trajectory);
    }
}
