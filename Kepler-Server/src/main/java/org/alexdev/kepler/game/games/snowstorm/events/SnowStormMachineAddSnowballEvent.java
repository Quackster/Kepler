package org.alexdev.kepler.game.games.snowstorm.events;

import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class SnowStormMachineAddSnowballEvent extends GameObject {
    public SnowStormMachineAddSnowballEvent(int machineId) {
        super(machineId, GameObjectType.SNOWWAR_MACHINE_ADD_SNOWBALL_EVENT);
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(this.getGameObjectType().getObjectId());
        response.writeInt(getId());
    }
}
