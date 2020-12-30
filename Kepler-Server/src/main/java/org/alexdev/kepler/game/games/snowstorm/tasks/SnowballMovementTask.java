package org.alexdev.kepler.game.games.snowstorm.tasks;

import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.mapping.SnowStormPathfinder;
import org.alexdev.kepler.game.games.snowstorm.objects.SnowballObject;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.util.schedule.FutureRunnable;

import java.util.LinkedList;

public class SnowballMovementTask extends FutureRunnable {
    private final SnowballObject snowball;
    private final Position firstPosition;
    private LinkedList<Position> path;
    private long thrownTime;
    private Position lastPosition;

    public SnowballMovementTask(SnowballObject snowball) {
        this.snowball = snowball;
        this.thrownTime = System.currentTimeMillis() + this.snowball.getTimeToLive() * 100;
        this.path = snowball.getPath();
        this.firstPosition = new Position(this.snowball.getFromX(), this.snowball.getFromY(), this.snowball.getDirection());
        this.lastPosition = new Position(this.snowball.getFromX(), this.snowball.getFromY(), this.snowball.getDirection());
    }

    @Override
    public void run() {
        if (snowball.getTrajectory() != SnowballObject.SnowballTrajectory.LONG_TRAJECTORY) {
            if (this.path.size() > 0) {
                var nextPosition = this.path.poll();

                this.lastPosition.setX(nextPosition.getX());
                this.lastPosition.setY(nextPosition.getY());

                // --var tile = this.snowball.getGame().getMap().getTile(nextPosition);

                // --if (tile != null) {
                // --    System.out.println("Trajectory: " + this.snowball.getTrajectory().name());
                // --    System.out.println("Tile: " + (tile.getHighestItem() != null ? tile.getHighestItem().getItemName() : "NULL") + " - " + nextPosition);
                // --    System.out.println("Height: " + (tile.getHighestItem() != null ? tile.getHighestItem().getHeight() : "NULL") + " - " + nextPosition);
                // -- }

                if (SnowStormPathfinder.isBlockedTile(this.snowball, nextPosition)) {
                    this.snowball.setTargetX(nextPosition.getX());
                    this.snowball.setTargetY(nextPosition.getY());
                    this.snowball.setBlocked(true);
                    this.endMovement(this.snowball, true);
                    return;
                }
            } else {
                // --System.out.println("Finished flying");
                if ((this.snowball.getTrajectory() == SnowballObject.SnowballTrajectory.QUICK_THROW || this.snowball.getTrajectory() == SnowballObject.SnowballTrajectory.LONG_TRAJECTORY) && continueQuickThrowSnowball()) {
                    this.path = this.snowball.getPath();
                } else {
                    endMovement(this.snowball, true);
                }

                return;
            }
        }

        if (System.currentTimeMillis() > this.thrownTime) {
            if (this.snowball.getTrajectory() == SnowballObject.SnowballTrajectory.QUICK_THROW && continueQuickThrowSnowball()) {
                    this.path = this.snowball.getPath();
            } else {
                endMovement(this.snowball, true);
            }
        }
    }

    private boolean continueQuickThrowSnowball() {
        boolean hasPlayer = false;

        Position nextPosition = new Position(this.snowball.getFromX(), this.snowball.getFromY(), 0, this.snowball.getDirection(), this.snowball.getDirection());

        // Only check in straight lines... otherwise it's a goddamn heat seeking missile
        while (true) {
            var tile = nextPosition;

            if (snowball.getGame().getMap().getTile(nextPosition) == null) {
                break;
            }

            var oppositionPlayer = snowball.getGame().getActivePlayers().stream()
                    .filter(gamePlayer -> gamePlayer.getSnowStormAttributes().getCurrentPosition().equals(new Position(tile.getX(), tile.getY())) &&
                                            gamePlayer.getPlayer().getDetails().getId() == snowball.getTargetPlayer().getPlayer().getDetails().getId() &&
                            gamePlayer.getSnowStormAttributes().isDamageable()).findFirst()
                    .orElse(null);
            //SnowStormPathfinder.getOppositionPlayer(snowball.getGame(), snowball.getThrower(), nextPosition);

            if (oppositionPlayer != null) {
                if (oppositionPlayer.getSnowStormAttributes().getCurrentPosition().getDistanceSquared(this.firstPosition) >= SnowStormGame.MAX_QUICK_THROW_DISTANCE) {
                    return false;
                }

                this.snowball.setFromX(this.lastPosition.getX());
                this.snowball.setFromY(this.lastPosition.getY());

                this.snowball.setTargetX(oppositionPlayer.getSnowStormAttributes().getCurrentPosition().getX());
                this.snowball.setTargetY(oppositionPlayer.getSnowStormAttributes().getCurrentPosition().getY());
                this.thrownTime = System.currentTimeMillis() + this.snowball.getTimeToLive() * 100;
                return true;
            }

            nextPosition = nextPosition.getSquareInFront();
        }

        return false;
    }

    private void endMovement(SnowballObject snowball, boolean deleteAfterHit) {
        this.snowball.getGame().handleSnowballLanding(this.snowball, deleteAfterHit);
        this.cancelFuture();
    }
}
