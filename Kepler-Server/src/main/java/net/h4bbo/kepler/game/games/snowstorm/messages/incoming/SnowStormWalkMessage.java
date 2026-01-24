package net.h4bbo.kepler.game.games.snowstorm.messages.incoming;

import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormPlayers;
import net.h4bbo.kepler.game.games.snowstorm.mapping.SnowStormPathfinder;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormAttributes;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormMessage;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormMath;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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

