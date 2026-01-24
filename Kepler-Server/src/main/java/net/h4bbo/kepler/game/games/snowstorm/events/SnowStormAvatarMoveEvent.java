package net.h4bbo.kepler.game.games.snowstorm.events;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class SnowStormAvatarMoveEvent extends GameObject {
    private final int x;
    private final int y;

    public SnowStormAvatarMoveEvent(int objectId, int x, int y) {
        super(objectId, GameObjectType.SNOWWAR_AVATAR_MOVE_EVENT);
        this.x = x;
        this.y = y;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(GameObjectType.SNOWWAR_AVATAR_MOVE_EVENT.getObjectId());
        response.writeInt(getId());
        response.writeInt(x);
        response.writeInt(y);
    }
}

