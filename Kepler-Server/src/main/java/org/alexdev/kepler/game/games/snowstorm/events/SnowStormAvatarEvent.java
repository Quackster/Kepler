package org.alexdev.kepler.game.games.snowstorm.events;

import org.alexdev.kepler.game.games.snowstorm.SnowStormObject;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class SnowStormAvatarEvent extends SnowStormObject {
    public SnowStormAvatarEvent() {
        super(null);
    }

    @Override
    public void refreshSyncValues() { }

    @Override
    public void serialiseObject(NettyResponse response) {
    }
}
