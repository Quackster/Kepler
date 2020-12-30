package org.alexdev.kepler.game.games.snowstorm.events;

import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class SnowStormCreateSnowballEvent extends GameObject {
    private final int objectId;

    public SnowStormCreateSnowballEvent(int objectId) {
        super(objectId, GameObjectType.SNOWWAR_CREATE_SNOWBALL_EVENT);
        this.objectId = objectId;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(GameObjectType.SNOWWAR_CREATE_SNOWBALL_EVENT.getObjectId());
        response.writeInt(this.objectId);
    }
}
