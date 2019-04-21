package org.alexdev.kepler.game.games.snowstorm.events;

import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormObject;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class SnowStormObjectEvent extends SnowStormObject {
    private final SnowStormObject object;

    public SnowStormObjectEvent(SnowStormObject object) {
        super(GameObjectType.SNOWWAR_AVATAR_MOVE_EVENT);
        this.object = object;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(0);
        this.object.serialiseObject(response);
    }

    @Override
    public List<Integer> getGameObjectsSyncValues() {
        return null;
    }
}
