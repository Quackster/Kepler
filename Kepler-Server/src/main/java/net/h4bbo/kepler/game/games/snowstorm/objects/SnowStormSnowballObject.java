package net.h4bbo.kepler.game.games.snowstorm.objects;

import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormConstants;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormItemProperties;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormMath;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a snowball projectile in the SnowStorm game.
 */
public class SnowStormSnowballObject extends SnowStormGameObject {
    private final SnowStormGame game;
    private final GamePlayer thrower;
    private int fromX;
    private int fromY;
    private int targetX;
    private int targetY;
    private final int trajectory;

    private int ballLocH;
    private int ballLocV;
    private int ballHeight;
    private int movementDirection;
    private int timeToLive;
    private int parabolaOffset;
    private boolean alive;

    public SnowStormSnowballObject(int objectId, SnowStormGame game, GamePlayer thrower,
                                   int fromX, int fromY, int targetX, int targetY, int trajectory) {
        super(objectId, GameObjectType.SNOWWAR_SNOWBALL_OBJECT);
        this.game = game;
        this.thrower = thrower;
        this.fromX = fromX;
        this.fromY = fromY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.trajectory = trajectory;
        this.alive = true;
        initializeFlightPath();
    }

    private void initializeFlightPath() {
        int type;
        int initialHeight;

        switch (this.trajectory) {
            case 1:
                type = 2;
                initialHeight = 3000;
                break;
            case 2:
                type = 3;
                initialHeight = 3000;
                break;
            default:
                type = 1;
                initialHeight = 4000;
                break;
        }

        int tLocH = SnowStormMath.tileToWorld(this.fromX);
        int tLocV = SnowStormMath.tileToWorld(this.fromY);

        Integer[] flightPath = SnowStormMath.calculateFlightPath(
                this.fromX, this.fromY, this.targetX, this.targetY, type);

        this.ballLocH = tLocH;
        this.ballLocV = tLocV;
        this.movementDirection = flightPath[0];
        this.timeToLive = flightPath[1];
        this.parabolaOffset = flightPath[2];
        this.ballHeight = initialHeight;
    }

    @Override
    public void calculateFrameMovement() {
        if (!alive) {
            return;
        }

        this.timeToLive--;

        int deltaX = (SnowStormMath.getBaseVelX(this.movementDirection) * SnowStormConstants.BASE_VELOCITY_MULTIPLIER) / SnowStormConstants.VELOCITY_DIVISOR;
        int deltaY = (SnowStormMath.getBaseVelY(this.movementDirection) * SnowStormConstants.BASE_VELOCITY_MULTIPLIER) / SnowStormConstants.VELOCITY_DIVISOR;

        int tNewX = this.ballLocH + deltaX;
        int tNewY = this.ballLocV + deltaY;
        int tTemp = this.timeToLive - this.parabolaOffset;
        int heightMultiplier = 100;
        int baseHeight = 3000;

        if (this.trajectory == 0) {
            tTemp = this.timeToLive > 3 ? 3 - this.parabolaOffset : tTemp;
            heightMultiplier = 4;
            baseHeight = 4000;
        } else if (this.trajectory == 1) {
            heightMultiplier = 10;
        }

        int tNewZ = ((((this.parabolaOffset * this.parabolaOffset) - (tTemp * tTemp)) * heightMultiplier) + baseHeight);

        this.ballLocH = tNewX;
        this.ballLocV = tNewY;
        this.ballHeight = tNewZ;

        if (testGroundCollision()) {
            this.alive = false;
        }
    }

    private boolean testGroundCollision() {
        int snowBallX = SnowStormMath.worldToTile(this.ballLocH);
        int snowBallY = SnowStormMath.worldToTile(this.ballLocV);

        if (this.ballHeight < 1) {
            return true;
        }

        for (var item : game.getMap().getItems()) {
            int collisionHeight = SnowStormItemProperties.getCollisionHeight(item.getItemName());
            if (collisionHeight < 0) {
                continue;
            }

            if (snowBallX == item.getX() && snowBallY == item.getY() && this.ballHeight < collisionHeight) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Integer> getChecksumValues() {
        List<Integer> values = getSyncValuesContainer();
        values.add(GameObjectType.SNOWWAR_SNOWBALL_OBJECT.getObjectId()); // type
        values.add(getId());
        values.add(ballLocH);
        values.add(ballLocV);
        values.add(ballHeight);
        values.add(movementDirection);
        values.add(trajectory);
        values.add(timeToLive);
        values.add(thrower.getObjectId());
        values.add(parabolaOffset);
        return values;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(GameObjectType.SNOWWAR_SNOWBALL_OBJECT.getObjectId());
        response.writeInt(getId());
        response.writeInt(ballLocH);
        response.writeInt(ballLocV);
        response.writeInt(ballHeight);
        response.writeInt(movementDirection);
        response.writeInt(trajectory);
        response.writeInt(timeToLive);
        response.writeInt(thrower.getObjectId());
        response.writeInt(parabolaOffset);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getLocH() {
        return ballLocH;
    }

    public int getLocV() {
        return ballLocV;
    }

    public int getBallHeight() {
        return ballHeight;
    }

    public int getMovementDirection() {
        return movementDirection;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public int getParabolaOffset() {
        return parabolaOffset;
    }

    public int getTrajectory() {
        return trajectory;
    }

    public GamePlayer getThrower() {
        return thrower;
    }

    public SnowStormGame getGame() {
        return game;
    }

    public int getFromX() {
        return fromX;
    }

    public void setFromX(int fromX) {
        this.fromX = fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public void setFromY(int fromY) {
        this.fromY = fromY;
    }

    public int getTargetX() {
        return targetX;
    }

    public void setTargetX(int targetX) {
        this.targetX = targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public void setTargetY(int targetY) {
        this.targetY = targetY;
    }

    public int getObjectId() {
        return getId();
    }
}
