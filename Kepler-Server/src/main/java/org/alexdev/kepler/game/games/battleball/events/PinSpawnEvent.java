package org.alexdev.kepler.game.games.battleball.events;

import org.alexdev.kepler.game.games.GameEvent;
import org.alexdev.kepler.game.games.battleball.objects.PinObject;
import org.alexdev.kepler.game.games.enums.GameEventType;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class PinSpawnEvent extends GameEvent {
    private final int id;
    private final Position position;

    public PinSpawnEvent(int id, Position position) {
        super(GameEventType.BATTLEBALL_OBJECT_SPAWN);
        this.id = id;
        this.position = position;
    }

    @Override
    public void serialiseEvent(NettyResponse response) {
        response.writeInt(2);
        new PinObject(this.id, this.position).serialiseObject(response);
    }
}
