package org.alexdev.kepler.game.games.snowstorm.events;

import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.game.games.snowstorm.SnowStormObject;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class SnowStormObjectEvent extends SnowStormObject {
    private final SnowStormObject object;

    public SnowStormObjectEvent(SnowStormObject object) {
        super(GameObjectType.SNOWWAR_EVENT_OBJECT);
        this.object = object;
    }

    @Override
    public void refreshSyncValues() { }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(0);
        object.serialiseObject(response);
    }
}
