package net.h4bbo.kepler.game.games.snowstorm.objects;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.concurrent.atomic.AtomicInteger;

public class SnowStormMachineObject extends GameObject {
    private final int objectId;
    private final int X;
    private final int Y;
    private AtomicInteger snowballCount;
    private long lastRefillTime;

    public SnowStormMachineObject(int objectId, int X, int Y, int snowballCount) {
        super(objectId, GameObjectType.SNOWWAR_SNOWMACHINE_OBJECT);
        this.objectId = objectId;
        this.X = X;
        this.Y = Y;
        this.snowballCount = new AtomicInteger(snowballCount);
        this.lastRefillTime = 0;
    }
    
    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(GameObjectType.SNOWWAR_SNOWMACHINE_OBJECT.getObjectId());
        response.writeInt(this.objectId);
        response.writeInt(SnowStormGame.convertToWorldCoordinate(this.X));
        response.writeInt(SnowStormGame.convertToWorldCoordinate(this.Y));
        response.writeInt(this.snowballCount.get());
    }

    public long getLastRefillTime() {
        return lastRefillTime;
    }

    public void setLastRefillTime(long lastRefillTime) {
        this.lastRefillTime = lastRefillTime;
    }

    public Position getPosition() {
        return new Position(X, Y);
    }

    public AtomicInteger getSnowballs() {
        return snowballCount;
    }

    public boolean isPlayerCollectingSnowballs(SnowStormGame game) {
        var playerPosition = new Position(this.X, this.Y + 1);
        var tile = game.getMap().getTile(playerPosition);

        if (tile == null) {
            return false;
        }

        var player = game.getActivePlayers().stream().filter(p -> p.getSnowStormAttributes().getCurrentPosition().equals(playerPosition)).findFirst().orElse(null);

        if (player == null || !player.getSnowStormAttributes().isWalkable()) {
            return false;
        }

        return true;
    }
}
