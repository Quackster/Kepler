package net.h4bbo.kepler.game.games.snowstorm.objects;

import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormDelayedEvent;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormPlayers;
import net.h4bbo.kepler.game.games.snowstorm.mapping.SnowStormPathfinder;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormActivityState;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormAttributes;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormConstants;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormMath;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player avatar in the SnowStorm game.
 */
public class SnowStormAvatarObject extends SnowStormGameObject {
    private final GamePlayer player;

    public SnowStormAvatarObject(GamePlayer gamePlayer) {
        super(gamePlayer.getObjectId(), GameObjectType.SNOWWAR_AVATAR_OBJECT);
        this.player = gamePlayer;
    }

    @Override
    public void calculateFrameMovement() {
        var attributes = SnowStormPlayers.get(player);

        // Handle activity timer
        int activityTimer = attributes.getActivityTimer();
        if (activityTimer > 0) {
            if (activityTimer == 1) {
                activityTimerTriggered();
            }
            attributes.decreaseActivityTimer();
        }

        // Check if we have a walking target
        if (existsFinalTarget()) {
            if (getStateAllowsMoving()) {
                boolean moving = calculateMovement();

                if (!moving) {
                    stopWalking();
                }
            }
        } else {
            if (attributes.isWalking()) {
                stopWalking();
            }
        }
    }

    private boolean existsFinalTarget() {
        var attributes = SnowStormPlayers.get(player);
        return attributes.getWalkGoal() != null;
    }

    private boolean getStateAllowsMoving() {
        var attributes = SnowStormPlayers.get(player);
        var state = attributes.getActivityState();

        return state == SnowStormActivityState.ACTIVITY_STATE_NORMAL
                || state == SnowStormActivityState.ACTIVITY_STATE_CREATING
                || state == SnowStormActivityState.ACTIVITY_STATE_INVINCIBLE_AFTER_STUN;
    }

    private void activityTimerTriggered() {
        var attr = SnowStormPlayers.get(player);

        switch (attr.getActivityState()) {
            case ACTIVITY_STATE_CREATING -> {
                attr.setActivityState(SnowStormActivityState.ACTIVITY_STATE_NORMAL);
                attr.getSnowballs().incrementAndGet();
            }
            case ACTIVITY_STATE_INVINCIBLE_AFTER_STUN -> {
                attr.setActivityState(SnowStormActivityState.ACTIVITY_STATE_NORMAL);
                attr.setStunnedToImplement(false);
            }
            case ACTIVITY_STATE_STUNNED -> {
                attr.setActivityState(SnowStormActivityState.ACTIVITY_STATE_INVINCIBLE_AFTER_STUN);
                attr.setActivityTimer(SnowStormConstants.INVINCIBILITY_TIMER);
                attr.getHealth().set(SnowStormConstants.INITIAL_HEALTH);
                attr.getHealthToImplement().set(SnowStormConstants.INITIAL_HEALTH);
            }
            default -> {
            }
        }
    }

    private boolean calculateMovement() {
        SnowStormGame game = getGame();
        if (game == null) {
            return false;
        }

        var attributes = SnowStormPlayers.get(player);

        Position moveTarget = attributes.getWalkGoal();
        Position nextTarget = attributes.getNextGoal();

        if (moveTarget == null) {
            return false;
        }

        // Get world coordinates for final target
        int moveTargetX = SnowStormMath.tileToWorld(moveTarget.getX());
        int moveTargetY = SnowStormMath.tileToWorld(moveTarget.getY());

        // Get current world position
        Position worldLoc = attributes.getGameObjectWorldLocation();
        int currentX;
        int currentY;

        if (worldLoc != null) {
            currentX = worldLoc.getX();
            currentY = worldLoc.getY();
        } else {
            Position currentPos = attributes.getCurrentPosition();
            currentX = SnowStormMath.tileToWorld(currentPos.getX());
            currentY = SnowStormMath.tileToWorld(currentPos.getY());
        }

        // Already at destination?
        if (currentX == moveTargetX && currentY == moveTargetY) {
            return false;
        }

        // Get next target world coordinates
        int nextTargetX;
        int nextTargetY;

        if (nextTarget != null) {
            nextTargetX = SnowStormMath.tileToWorld(nextTarget.getX());
            nextTargetY = SnowStormMath.tileToWorld(nextTarget.getY());
        } else {
            // No next target, use current position
            Position currentPos = attributes.getCurrentPosition();
            nextTargetX = SnowStormMath.tileToWorld(currentPos.getX());
            nextTargetY = SnowStormMath.tileToWorld(currentPos.getY());
        }

        // If not at next target tile (world coords differ), move towards it
        if (nextTargetX != currentX || nextTargetY != currentY) {
            // Move X towards next target
            int deltaX = nextTargetX - currentX;
            if (deltaX < 0) {
                if (deltaX > -SnowStormConstants.SUBTURN_MOVEMENT) {
                    currentX = nextTargetX;
                } else {
                    currentX = currentX - SnowStormConstants.SUBTURN_MOVEMENT;
                }
            } else if (deltaX > 0) {
                if (deltaX < SnowStormConstants.SUBTURN_MOVEMENT) {
                    currentX = nextTargetX;
                } else {
                    currentX = currentX + SnowStormConstants.SUBTURN_MOVEMENT;
                }
            }

            // Move Y towards next target
            int deltaY = nextTargetY - currentY;
            if (deltaY < 0) {
                if (deltaY > -SnowStormConstants.SUBTURN_MOVEMENT) {
                    currentY = nextTargetY;
                } else {
                    currentY = currentY - SnowStormConstants.SUBTURN_MOVEMENT;
                }
            } else if (deltaY > 0) {
                if (deltaY < SnowStormConstants.SUBTURN_MOVEMENT) {
                    currentY = nextTargetY;
                } else {
                    currentY = currentY + SnowStormConstants.SUBTURN_MOVEMENT;
                }
            }

            // Update world location
            attributes.setGameObjectWorldLocation(new Position(currentX, currentY));

            // Update tile position if we've crossed into a new tile
            int newTileX = SnowStormMath.worldToTile(currentX);
            int newTileY = SnowStormMath.worldToTile(currentY);
            Position currentPos = attributes.getCurrentPosition();

            if (newTileX != currentPos.getX() || newTileY != currentPos.getY()) {
                attributes.setCurrentPosition(new Position(newTileX, newTileY));
            }

            // Reached final target?
            return currentX != moveTargetX || currentY != moveTargetY;
        } else {
            // At next target tile, need to find new next target towards final destination
            Position currentPos = attributes.getCurrentPosition();
            int tileX = currentPos.getX();
            int tileY = currentPos.getY();

            // Calculate direction to final target
            int moveDirection360 = SnowStormMath.getAngleFromComponents(
                    moveTargetX - currentX,
                    moveTargetY - currentY
            );
            int nextDir = SnowStormMath.direction360To8(moveDirection360);

            // Try to get tile in that direction
            Integer[] nextTile = SnowStormPathfinder.getTileNeighborInDirection(
                    game, player, tileX, tileY, nextDir
            );

            // If blocked, try 45 degrees CCW
            if (nextTile == null) {
                int ccwDir = SnowStormMath.direction360To8(
                        SnowStormMath.rotateDirection45DegreesCcw(moveDirection360)
                );
                nextTile = SnowStormPathfinder.getTileNeighborInDirection(
                        game, player, tileX, tileY, ccwDir
                );

                if (nextTile != null) {
                    nextDir = ccwDir;
                }
            }

            // If still blocked, try 45 degrees CW
            if (nextTile == null) {
                int cwDir = SnowStormMath.direction360To8(
                        SnowStormMath.rotateDirection45DegreesCw(moveDirection360)
                );
                nextTile = SnowStormPathfinder.getTileNeighborInDirection(
                        game, player, tileX, tileY, cwDir
                );

                if (nextTile != null) {
                    nextDir = cwDir;
                }
            }

            // If found a valid next tile, set it and recurse
            if (nextTile != null) {
                attributes.setRotation(nextDir);
                attributes.setNextGoal(new Position(nextTile[0], nextTile[1]));
                return calculateMovement();
            }
        }

        return false;
    }

    @Override
    public List<Integer> getChecksumValues() {
        var attributes = SnowStormPlayers.get(player);
        var nextCoords = attributes.getNextGoal();
        var nextGoal = attributes.getWalkGoal();
        var currentPosition = attributes.getCurrentPosition();

        int checksumTargetX = nextCoords != null ? nextCoords.getX() : currentPosition.getX();
        int checksumTargetY = nextCoords != null ? nextCoords.getY() : currentPosition.getY();
        int checksumWalkX = nextGoal != null ? nextGoal.getX() : currentPosition.getX();
        int checksumWalkY = nextGoal != null ? nextGoal.getY() : currentPosition.getY();

        List<Integer> values = getSyncValuesContainer();
        values.add(GameObjectType.SNOWWAR_AVATAR_OBJECT.getObjectId()); // type
        values.add(player.getObjectId());
        values.add(SnowStormMath.tileToWorld(checksumTargetX));
        values.add(SnowStormMath.tileToWorld(checksumTargetY));
        values.add(attributes.getRotation());
        values.add(attributes.getHealth().get());
        values.add(attributes.getSnowballs().get());
        values.add(0); // is_bot
        values.add(attributes.getActivityTimer());
        values.add(attributes.getActivityState().getStateId());
        values.add(checksumTargetX);
        values.add(checksumTargetY);
        values.add(SnowStormMath.tileToWorld(checksumWalkX));
        values.add(SnowStormMath.tileToWorld(checksumWalkY));
        values.add(attributes.getScore().get());
        values.add(player.getUserId());
        values.add(player.getTeamId());
        values.add(player.getObjectId());
        return values;
    }

    @Override
    public boolean isAlive() {
        return true; // Players are always active (they respawn after stun)
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        var attributes = SnowStormPlayers.get(this.player);
        var nextGoal = attributes.getNextGoal();
        var currentPosition = attributes.getCurrentPosition();
        var moveTarget = attributes.isWalking() && attributes.getWalkGoal() != null
                ? attributes.getWalkGoal()
                : currentPosition;

        response.writeInt(GameObjectType.SNOWWAR_AVATAR_OBJECT.getObjectId()); // type id
        response.writeInt(this.player.getObjectId()); // int id
        response.writeInt(SnowStormMath.convertToWorldCoordinate(currentPosition.getX()));
        response.writeInt(SnowStormMath.convertToWorldCoordinate(currentPosition.getY()));
        response.writeInt(attributes.getRotation()); // body direction
        response.writeInt(attributes.getHealth().get()); // hit points
        response.writeInt(attributes.getSnowballs().get()); // snowball count
        response.writeInt(0); // is bot
        response.writeInt(attributes.getActivityTimer()); // activity timer
        response.writeInt(attributes.getActivityState().getStateId()); // activity state
        response.writeInt(nextGoal != null ? nextGoal.getX() : currentPosition.getX()); // move target x
        response.writeInt(nextGoal != null ? nextGoal.getY() : currentPosition.getY()); // move target y
        response.writeInt(SnowStormMath.convertToWorldCoordinate(moveTarget.getX())); // move target x
        response.writeInt(SnowStormMath.convertToWorldCoordinate(moveTarget.getY())); // move target y
        response.writeInt(attributes.getScore().get()); // score
        response.writeInt(player.getPlayer().getDetails().getId()); // player id
        response.writeInt(player.getTeamId()); // team id
        response.writeInt(player.getObjectId()); // room index
        response.writeString(player.getPlayer().getDetails().getName());
        response.writeString(player.getPlayer().getDetails().getMotto());
        response.writeString(player.getPlayer().getDetails().getFigure());
        response.writeString(player.getPlayer().getDetails().getSex());// Actually room user id/instance id
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public static SnowStormAvatarObject getAvatar(GamePlayer player) {
        if (player == null) {
            return null;
        }

        if (player.getGameObject() instanceof SnowStormAvatarObject avatar) {
            return avatar;
        }

        return null;
    }

    public void stopWalking() {
        stopWalkingForPlayer(player);
    }

    public boolean testCircleToCircleCollision(SnowStormSnowballObject ball) {
        SnowStormGame game = getGame();
        if (game == null) {
            return false;
        }

        if (ball.getBallHeight() > SnowStormConstants.BALL_HEIGHT_COLLISION_THRESHOLD) {
            return false;
        }

        if (!game.isOppositionPlayer(player, ball.getThrower())) {
            return false;
        }

        SnowStormAttributes attributes = SnowStormPlayers.get(player);
        Position nextCoords = attributes.getNextGoal();
        int currentX;
        int currentY;

        if (nextCoords == null) {
            var currentPosition = attributes.getCurrentPosition();
            currentX = SnowStormMath.tileToWorld(currentPosition.getX());
            currentY = SnowStormMath.tileToWorld(currentPosition.getY());
        } else {
            var worldPosition = attributes.getGameObjectWorldLocation();
            if (worldPosition == null) {
                var currentPosition = attributes.getCurrentPosition();
                currentX = SnowStormMath.tileToWorld(currentPosition.getX());
                currentY = SnowStormMath.tileToWorld(currentPosition.getY());
            } else {
                currentX = worldPosition.getX();
                currentY = worldPosition.getY();
            }
        }

        int distanceX = Math.abs(ball.getLocH() - currentX);
        int distanceY = Math.abs(ball.getLocV() - currentY);
        int collisionDistance = SnowStormConstants.COLLISION_DISTANCE;
        return distanceY < collisionDistance
                && distanceX < collisionDistance
                && distanceX * distanceX + distanceY * distanceY < collisionDistance * collisionDistance;
    }

    public List<SnowStormDelayedEvent> checkCollisions(List<SnowStormSnowballObject> snowBalls) {
        List<SnowStormDelayedEvent> delayedEvents = new ArrayList<>();

        if (snowBalls == null || snowBalls.isEmpty()) {
            return delayedEvents;
        }

        if (getGame() == null || isPlayerImmune()) {
            return delayedEvents;
        }

        for (SnowStormSnowballObject ball : snowBalls) {
            if (testCircleToCircleCollision(ball)) {
                processHit(ball, delayedEvents);
            }
        }

        return delayedEvents;
    }

    public static void handleForSnowballCollisions(List<SnowStormDelayedEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        for (SnowStormDelayedEvent event : events) {
            switch (event.getEventType()) {
                case HIT -> processHitEvent(event);
                case STUN -> processStunEvent(event);
            }
        }
    }

    private boolean isPlayerImmune() {
        var state = SnowStormPlayers.get(player).getActivityState();

        return state == SnowStormActivityState.ACTIVITY_STATE_STUNNED
                || state == SnowStormActivityState.ACTIVITY_STATE_INVINCIBLE_AFTER_STUN;
    }

    private void processHit(SnowStormSnowballObject ball, List<SnowStormDelayedEvent> delayedEvents) {
        var attributes = SnowStormPlayers.get(player);

        if (attributes.getHealthToImplement().get() > 0) {
            applyDamage(ball);
            scheduleHitEvent(delayedEvents, player, ball);
            return;
        }

        if (!attributes.getStunnedToImplement()) {
            applyStun(ball);
            scheduleStunEvent(delayedEvents, player, ball);
        }
    }

    private void applyDamage(SnowStormSnowballObject ball) {
        var attributes = SnowStormPlayers.get(player);
        SnowStormGame game = getGame();
        if (game == null) {
            return;
        }

        attributes.getHealthToImplement().decrementAndGet();
        game.hitPlayer(ball, player);
    }

    private void applyStun(SnowStormSnowballObject ball) {
        var attributes = SnowStormPlayers.get(player);
        SnowStormGame game = getGame();
        if (game == null) {
            return;
        }

        attributes.setStunnedToImplement(true);

        Position lastTilePosition = new Position(
                ball.getTargetX(),
                ball.getTargetY()
        );

        game.stunPlayerHandler(
                game,
                ball.getThrower(),
                player,
                lastTilePosition,
                ball
        );

        stopWalking();
    }

    private static void scheduleHitEvent(
            List<SnowStormDelayedEvent> events,
            GamePlayer player,
            SnowStormSnowballObject ball) {

        events.add(SnowStormDelayedEvent.hit(player, ball));
    }

    private static void scheduleStunEvent(
            List<SnowStormDelayedEvent> events,
            GamePlayer player,
            SnowStormSnowballObject ball) {

        events.add(SnowStormDelayedEvent.stun(player, ball));
    }

    private static void processHitEvent(SnowStormDelayedEvent event) {
        GamePlayer player = event.getPlayer();
        SnowStormSnowballObject ball = event.getBall();

        var attributes = SnowStormPlayers.get(player);
        attributes.getHealth().set(attributes.getHealthToImplement().get());

        SnowStormPlayers.get(ball.getThrower())
                .getScore()
                .addAndGet(SnowStormConstants.HIT_SCORE);
    }

    private static void processStunEvent(SnowStormDelayedEvent event) {
        GamePlayer player = event.getPlayer();
        var attributes = SnowStormPlayers.get(player);

        if (attributes.isWalking()) {
            SnowStormAvatarObject avatar = getAvatar(player);
            if (avatar != null) {
                avatar.stopWalking();
            } else {
                stopWalkingForPlayer(player);
            }
        }

        SnowStormPlayers.get(event.getBall().getThrower())
                .getScore()
                .addAndGet(SnowStormConstants.STUN_SCORE);

        attributes.getSnowballs().set(0);
        attributes.setActivityTimer(SnowStormConstants.STUNNED_TIMER);
        attributes.setActivityState(SnowStormActivityState.ACTIVITY_STATE_STUNNED);
    }

    private static int moveTowards(int current, int target) {
        int delta = target - current;
        if (delta == 0) {
            return current;
        }

        int step = SnowStormConstants.SUBTURN_MOVEMENT;
        if (Math.abs(delta) <= step) {
            return target;
        }

        return current + (delta < 0 ? -step : step);
    }

    private SnowStormGame getGame() {
        if (player == null) {
            return null;
        }

        var game = player.getGame();
        if (game instanceof SnowStormGame snowStormGame) {
            return snowStormGame;
        }

        return null;
    }

    private static void stopWalkingForPlayer(GamePlayer player) {
        if (player == null) {
            return;
        }

        SnowStormAttributes attributes = SnowStormPlayers.get(player);
        attributes.setWalking(false);

        var nextGoal = attributes.getNextGoal();
        if (nextGoal != null) {
            attributes.setCurrentPosition(new Position(nextGoal.getX(), nextGoal.getY()));
        }

        attributes.setWalkGoal(null);
        attributes.setNextGoal(null);
    }
}
