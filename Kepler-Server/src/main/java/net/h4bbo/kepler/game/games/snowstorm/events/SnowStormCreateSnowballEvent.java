package net.h4bbo.kepler.game.games.snowstorm.events;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class SnowStormCreateSnowballEvent extends GameObject {
    public SnowStormCreateSnowballEvent(int objectId) {
        super(objectId, GameObjectType.SNOWWAR_CREATE_SNOWBALL_EVENT);
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(GameObjectType.SNOWWAR_CREATE_SNOWBALL_EVENT.getObjectId());
        response.writeInt(getId());
    }
}
