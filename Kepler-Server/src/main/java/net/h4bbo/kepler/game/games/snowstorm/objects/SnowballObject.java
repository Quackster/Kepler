package net.h4bbo.kepler.game.games.snowstorm.objects;

import net.h4bbo.kepler.game.GameScheduler;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.games.snowstorm.mapping.SnowStormPathfinder;
import net.h4bbo.kepler.game.games.snowstorm.tasks.SnowballMovementTask;
import net.h4bbo.kepler.game.pathfinder.Position;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class SnowballObject {
    public GamePlayer getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(GamePlayer targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public enum SnowballTrajectory {
        QUICK_THROW(0, 3000),
        SHORT_TRAJECTORY(1, 3000),
        LONG_TRAJECTORY(2, 3000);

        private final int trajectoryId;
        private final int velocity;

        SnowballTrajectory(int trajectoryId, int velocity) {
            this.trajectoryId = trajectoryId;
            this.velocity = velocity;
        }

        public int getVelocity() {
            return velocity;
        }

        public int getTrajectoryId() {
            return trajectoryId;
        }
    }

    private int objectId;
    private SnowStormGame game;
    private GamePlayer thrower;
    private int fromX;
    private int fromY;
    private int targetX;
    private int targetY;
    private int trajectory;
    private int direction;
    private boolean isBlocked;
    private GamePlayer targetPlayer;

    public SnowballObject(int objectId, SnowStormGame snowStormGame, GamePlayer thrower, int fromX, int fromY, int targetX, int targetY, int trajectory, int direction) {
        this.objectId = objectId;
        this.game = snowStormGame;
        this.thrower = thrower;
        this.fromX = fromX;
        this.fromY = fromY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.direction = direction;
        this.trajectory = trajectory;
    }

    public int getTimeToLive() {
        int tX = SnowStormGame.convertToWorldCoordinate(this.fromX);
        int tY = SnowStormGame.convertToWorldCoordinate(this.fromY);

        int tDeltaX = ((SnowStormGame.convertToWorldCoordinate(this.targetX) - tX) / 200);
        int tDeltaY = ((SnowStormGame.convertToWorldCoordinate(this.targetY) - tY) / 200);

        double tDistanceToTarget = Math.sqrt(((tDeltaX * tDeltaX) + (tDeltaY * tDeltaY))) * 200;

        if (this.getTrajectory() == SnowballTrajectory.QUICK_THROW) {
            return (int) (tDistanceToTarget / SnowballTrajectory.QUICK_THROW.getVelocity());
            //return SnowballTrajectory.QUICK_THROW.getTimeToLive();
        }

        if (this.getTrajectory() == SnowballTrajectory.SHORT_TRAJECTORY) {
            return (int) (tDistanceToTarget / SnowballTrajectory.SHORT_TRAJECTORY.getVelocity());

        }

        if (this.getTrajectory() == SnowballTrajectory.LONG_TRAJECTORY) {
            return (int) (tDistanceToTarget / SnowballTrajectory.LONG_TRAJECTORY.getVelocity());
        }

        return -1;
    }

    public LinkedList<Position> getPath() {
        return SnowStormPathfinder.getMaxVisibility(
                this,
                getFromX(),
                getFromY(),
                getTargetX(),
                getTargetY(),
                getTrajectory()
        );
    }

    public void scheduleMovementTask() {
        var path = this.getPath();

        if (this.getTimeToLive() > 0 && path.size() > 0) {
            var futureRunnable = new SnowballMovementTask(this);
                futureRunnable.setFuture(GameScheduler.getInstance().getService().scheduleAtFixedRate(futureRunnable, 0, (this.getTimeToLive() * 100) / path.size(), TimeUnit.MILLISECONDS));
        } else {
            this.game.handleSnowballLanding(this, false);
        }
    }

    public int getObjectId() {
        return objectId;
    }

    public GamePlayer getThrower() {
        return thrower;
    }

    public void setThrower(GamePlayer thrower) {
        this.thrower = thrower;
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

    public SnowballTrajectory getTrajectory() {
        if (this.trajectory == SnowballTrajectory.QUICK_THROW.trajectoryId) {
            return SnowballTrajectory.QUICK_THROW;
        }

        if (this.trajectory == SnowballTrajectory.SHORT_TRAJECTORY.trajectoryId) {
            return SnowballTrajectory.SHORT_TRAJECTORY;
        }

        if (this.trajectory == SnowballTrajectory.LONG_TRAJECTORY.trajectoryId) {
            return SnowballTrajectory.LONG_TRAJECTORY;
        }

        return null;
    }

    public void setTrajectory(int trajectory) {
        this.trajectory = trajectory;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public int getDirection() {
        return direction;
    }
}
