package org.alexdev.kepler.game.games.snowstorm.messages.incoming;

import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.SnowStormPlayers;
import org.alexdev.kepler.game.games.snowstorm.mapping.SnowStormPathfinder;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormAttributes;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormMessage;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormMath;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SnowStormWalkMessage implements SnowStormMessage {
    @Override
    public void handle(NettyRequest reader, SnowStormGame snowStormGame, GamePlayer gamePlayer) {
        SnowStormAttributes attributes = SnowStormPlayers.get(gamePlayer);

        if (!attributes.isWalkable() || !gamePlayer.getPlayer().getRoomUser().isWalkingAllowed()) {
            return;
        }

        int rawX = reader.readInt();
        int rawY = reader.readInt();

        int newX = SnowStormMath.convertToGameCoordinate(rawX);
        int newY = SnowStormMath.convertToGameCoordinate(rawY);

        Position newPosition = new Position(newX, newY);

        if (attributes.getCurrentPosition().equals(newPosition)) {
            return;
        }

        // Check if we can find a valid first step towards the destination
        if (!SnowStormPathfinder.canWalkTo(snowStormGame, gamePlayer, attributes.getCurrentPosition(), newPosition)) {
            return;
        }

        attributes.setGoalWorldCoordinates(new int[] { rawX, rawY });
        attributes.setWalking(true);
        attributes.setWalkGoal(newPosition);
    }

}

