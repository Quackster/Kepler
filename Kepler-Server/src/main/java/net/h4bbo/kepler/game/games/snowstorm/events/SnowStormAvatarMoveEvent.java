package net.h4bbo.kepler.game.games.snowstorm.events;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class SnowStormAvatarMoveEvent extends GameObject {
    private final int objectId;
    private int X;
    private int Y;

    public SnowStormAvatarMoveEvent(int objectId, int x, int y) {
        super(objectId, GameObjectType.SNOWWAR_AVATAR_MOVE_EVENT);
        this.objectId = objectId;
        this.X = x;
        this.Y = y;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(GameObjectType.SNOWWAR_AVATAR_MOVE_EVENT.getObjectId());
        response.writeInt(this.objectId);
        response.writeInt(X);//SnowStormGame.convertToWorldCoordinate(this.X)); // move target x
        response.writeInt(Y);//SnowStormGame.convertToWorldCoordinate(this.Y)); // move target y
    }
}
