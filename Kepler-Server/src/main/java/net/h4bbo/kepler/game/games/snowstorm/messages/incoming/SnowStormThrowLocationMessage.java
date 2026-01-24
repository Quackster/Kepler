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

public class SnowStormThrowLocationMessage implements SnowStormMessage {
    @Override
    public void handle(NettyRequest reader, SnowStormGame snowStormGame, GamePlayer gamePlayer) {
        var attributes = SnowStormPlayers.get(gamePlayer);
        if (!attributes.isWalkable()) {
            return;
        }

        if ((attributes.getLastThrow().get() + SnowStormConstants.THROW_COOLDOWN_MS) > System.currentTimeMillis()) {
            return;
        }

        int worldX = reader.readInt();
        int worldY = reader.readInt();
        int trajectory = reader.readInt();
        if (trajectory != 1 && trajectory != 2) {
            return;
        }

        if (attributes.getSnowballs().get() <= 0) {
            return;
        }

        if (attributes.isWalking()) {
            SnowStormAvatarObject avatar = SnowStormAvatarObject.getAvatar(gamePlayer);
            if (avatar != null) {
                avatar.stopWalking();
            }
        }

        int objectId = snowStormGame.getObjectId().incrementAndGet();
        int targetX = SnowStormMath.convertToGameCoordinate(worldX);
        int targetY = SnowStormMath.convertToGameCoordinate(worldY);

        var snowball = new SnowStormSnowballObject(
                objectId,
                snowStormGame,
                gamePlayer,
                attributes.getCurrentPosition().getX(),
                attributes.getCurrentPosition().getY(),
                targetX,
                targetY,
                trajectory);

        snowStormGame.getUpdateTask().addSnowball(snowball);
        attributes.getSnowballs().decrementAndGet();

        int currentWorldX = SnowStormMath.tileToWorld(attributes.getCurrentPosition().getX());
        int currentWorldY = SnowStormMath.tileToWorld(attributes.getCurrentPosition().getY());
        int moveDirection360 = SnowStormMath.getAngleFromComponents((worldX - currentWorldX), (worldY - currentWorldY));
        attributes.setRotation(SnowStormMath.direction360To8(moveDirection360));

        int convertedTargetX = SnowStormMath.convertToWorldCoordinate(snowball.getTargetX());
        int convertedTargetY = SnowStormMath.convertToWorldCoordinate(snowball.getTargetY());

        snowStormGame.getUpdateTask().queueEvent( new SnowStormThrowEvent(gamePlayer.getObjectId(), convertedTargetX, convertedTargetY, trajectory));
        snowStormGame.getUpdateTask().queueEvent( new SnowStormLaunchSnowballEvent(objectId, gamePlayer.getObjectId(), convertedTargetX, convertedTargetY, trajectory));
    }
}

