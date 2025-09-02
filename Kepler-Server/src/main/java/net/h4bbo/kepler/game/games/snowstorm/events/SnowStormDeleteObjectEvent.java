package net.h4bbo.kepler.game.games.snowstorm.events;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class SnowStormDeleteObjectEvent extends GameObject {
    private final int objectId;

    public SnowStormDeleteObjectEvent(int objectId) {
        super(objectId, GameObjectType.SNOWWAR_REMOVE_OBJECT_EVENT);
        this.objectId = objectId;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(this.getGameObjectType().getObjectId());
        response.writeInt(this.objectId);
    }
}
