package org.alexdev.kepler.game.games.snowstorm.messages.incoming;

import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.games.snowstorm.util.SnowStormMessage;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SnowStormWalkMessage implements SnowStormMessage {
    @Override
    public void handle(NettyRequest reader, SnowStormGame snowStormGame, GamePlayer gamePlayer) {
        if (!gamePlayer.getSnowStormAttributes().isWalkable() || !gamePlayer.getPlayer().getRoomUser().isWalkingAllowed()) {
            return;
        }

        int X = reader.readInt();
        int Y = reader.readInt();

        int newX = SnowStormGame.convertToGameCoordinate(X);
        int newY = SnowStormGame.convertToGameCoordinate(Y);

        // --System.out.println("Request: " + newX + ", " + newY);
        if (gamePlayer.getSnowStormAttributes().getCurrentPosition().equals(new Position(newX, newY))) {
            return;
        }

        gamePlayer.getSnowStormAttributes().setGoalWorldCoordinates(new int[] { X, Y });
        gamePlayer.getSnowStormAttributes().setWalking(true);
        gamePlayer.getSnowStormAttributes().setWalkGoal(new Position(newX, newY));

        //snowStormGame.getUpdateTask().sendQueue(0, 1,
        //        new SnowStormAvatarMoveEvent(gamePlayer.getObjectId(), X, Y));
    }
}
