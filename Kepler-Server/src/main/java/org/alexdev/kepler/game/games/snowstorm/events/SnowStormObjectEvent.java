package org.alexdev.kepler.game.games.snowstorm.events;

import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.game.games.snowstorm.SnowStormObject;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class SnowStormObjectEvent extends SnowStormObject {
    private final SnowStormObject obj;

    public SnowStormObjectEvent(SnowStormObject obj) {
        super(GameObjectType.SNOWWAR_OBJECT_EVENT);
        this.obj = obj;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(GameObjectType.SNOWWAR_OBJECT_EVENT.getObjectId());
        this.obj.serialiseObject(response);
    }
}
