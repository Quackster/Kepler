package org.alexdev.kepler.game.games.snowstorm.events;

import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormObject;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class SnowStormAvatarMoveEvent extends SnowStormObject {
    private final int objectId;
    private int X;
    private int Y;

    public SnowStormAvatarMoveEvent(int objectId, int x, int y) {
        super(GameObjectType.SNOWWAR_AVATAR_MOVE_EVENT);
        this.objectId = objectId;
        this.X = x;
        this.Y = y;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(GameObjectType.SNOWWAR_AVATAR_MOVE_EVENT.getObjectId());
        response.writeInt(this.objectId);
        response.writeInt(SnowStormGame.convertToWorldCoordinate(this.X)); // move target x
        response.writeInt(SnowStormGame.convertToWorldCoordinate(this.Y)); // move target y
    }

    @Override
    public List<Integer> getGameObjectsSyncValues() {
        return null;
    }
}
