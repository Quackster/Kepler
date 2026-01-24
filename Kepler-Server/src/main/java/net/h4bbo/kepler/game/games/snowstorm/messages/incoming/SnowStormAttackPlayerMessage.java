package org.alexdev.kepler.game.games.snowstorm.messages.incoming;

import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormPlayers;
import org.alexdev.kepler.game.games.snowstorm.objects.SnowStormAvatarObject;
import org.alexdev.kepler.game.games.snowstorm.events.SnowStormLaunchSnowballEvent;
import org.alexdev.kepler.game.games.snowstorm.events.SnowStormThrowEvent;
import org.alexdev.kepler.game.games.snowstorm.objects.SnowStormSnowballObject;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormConstants;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormMessage;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormMath;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SnowStormAttackPlayerMessage implements SnowStormMessage {
    private static final int QUICK_THROW_TRAJECTORY = 3;

    @Override
    public void handle(NettyRequest reader, SnowStormGame snowStormGame, GamePlayer gamePlayer) {
        var attributes = SnowStormPlayers.get(gamePlayer);
        if (!attributes.isWalkable()) {
            return;
        }

        if ((attributes.getLastThrow().get() + SnowStormConstants.THROW_COOLDOWN_MS) > System.currentTimeMillis()) {
            return;
        }

        int userId = reader.readInt();
        int trajectory = reader.readInt();
        if (!isValidTrajectory(trajectory)) {
            return;
        }

        if (attributes.getSnowballs().get() <= 0) {
            return;
        }

        var targetPlayer = snowStormGame.getActivePlayers().stream()
                .filter(p -> p.getObjectId() == userId)
                .findFirst()
                .orElse(null);

        if (targetPlayer == null || !snowStormGame.isOppositionPlayer(gamePlayer, targetPlayer)) {
            return;
        }

        if (attributes.isWalking()) {
            SnowStormAvatarObject avatar = SnowStormAvatarObject.getAvatar(gamePlayer);
            if (avatar != null) {
                avatar.stopWalking();
            }
        }

        int objectId = snowStormGame.getObjectId().incrementAndGet();
        var origin = attributes.getCurrentPosition();
        var destination = SnowStormPlayers.get(targetPlayer).getCurrentPosition();

        var snowball = new SnowStormSnowballObject(
                objectId,
                snowStormGame,
                gamePlayer,
                origin.getX(),
                origin.getY(),
                destination.getX(),
                destination.getY(),
                trajectory);

        snowStormGame.getUpdateTask().addSnowball(snowball);
        attributes.getSnowballs().decrementAndGet();

        int targetX = SnowStormMath.tileToWorld(destination.getX());
        int targetY = SnowStormMath.tileToWorld(destination.getY());
        int currentX = SnowStormMath.tileToWorld(origin.getX());
        int currentY = SnowStormMath.tileToWorld(origin.getY());

        int direction = SnowStormMath.getAngleFromComponents(targetX - currentX, targetY - currentY);
        attributes.setRotation(SnowStormMath.direction360To8(direction));

        int worldTargetX = SnowStormMath.convertToWorldCoordinate(snowball.getTargetX());
        int worldTargetY = SnowStormMath.convertToWorldCoordinate(snowball.getTargetY());
        snowStormGame.getUpdateTask().queueEvent( new SnowStormThrowEvent(gamePlayer.getObjectId(), worldTargetX, worldTargetY, trajectory));
        snowStormGame.getUpdateTask().queueEvent( new SnowStormLaunchSnowballEvent(objectId, gamePlayer.getObjectId(), worldTargetX, worldTargetY, trajectory));
    }

    private boolean isValidTrajectory(int trajectory) {
        return trajectory == 0 || trajectory == 2 || trajectory == QUICK_THROW_TRAJECTORY;
    }
}

