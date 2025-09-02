package net.h4bbo.kepler.game.games.snowstorm.messages.incoming;

import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.games.snowstorm.events.SnowStormLaunchSnowballEvent;
import net.h4bbo.kepler.game.games.snowstorm.events.SnowStormThrowEvent;
import net.h4bbo.kepler.game.games.snowstorm.objects.SnowballObject;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormMessage;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.game.pathfinder.Rotation;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class SnowStormThrowLocationMessage implements SnowStormMessage {
    @Override
    public void handle(NettyRequest reader, SnowStormGame snowStormGame, GamePlayer gamePlayer){
        if (!gamePlayer.getSnowStormAttributes().isWalkable()) {
            return;
        }

        if ((gamePlayer.getSnowStormAttributes().getLastThrow().get() + 300) > System.currentTimeMillis()) {
            return;
        }

        int X = reader.readInt();
        int Y = reader.readInt();
        int trajectory = reader.readInt();

        if (trajectory != 2 && trajectory != 1) {
            return;
        }

        if (gamePlayer.getSnowStormAttributes().getSnowballs().get() <= 0) {
            return;
        }

        //gamePlayer.getSnowStormAttributes().getPlayer().getRoomUser().setWalkingAllowed(false);
        int objectId = snowStormGame.getObjectId().incrementAndGet();

        final var snowball = new SnowballObject(
                objectId,
                snowStormGame,
                gamePlayer,
                gamePlayer.getSnowStormAttributes().getCurrentPosition().getX(),
                gamePlayer.getSnowStormAttributes().getCurrentPosition().getY(),
                SnowStormGame.convertToGameCoordinate(X),
                SnowStormGame.convertToGameCoordinate(Y),
                trajectory,
                Rotation.calculateWalkDirection(gamePlayer.getSnowStormAttributes().getCurrentPosition(), new Position(SnowStormGame.convertToGameCoordinate(X), SnowStormGame.convertToGameCoordinate(Y))));

        gamePlayer.getSnowStormAttributes().getSnowballs().decrementAndGet();

        var visibilityPath = snowball.getPath();
        Position lastTilePosition = visibilityPath.size() > 0 ? visibilityPath.pollLast() : null;

        // Reconsider velocity/time to live and recalculate since it's blocked
        if (lastTilePosition != null && !lastTilePosition.equals(new Position(snowball.getTargetX(), snowball.getTargetY()))) {
            snowball.setTargetX(lastTilePosition.getX());
            snowball.setTargetY(lastTilePosition.getY());
            snowball.setBlocked(true);
        }

        snowStormGame.getUpdateTask().sendQueue(0, 1, new SnowStormThrowEvent(gamePlayer.getObjectId(), SnowStormGame.convertToWorldCoordinate(snowball.getTargetX()), SnowStormGame.convertToWorldCoordinate(snowball.getTargetY()), trajectory));
        snowStormGame.getUpdateTask().sendQueue(0, 1, new SnowStormLaunchSnowballEvent(objectId, gamePlayer.getObjectId(), SnowStormGame.convertToWorldCoordinate(snowball.getTargetX()), SnowStormGame.convertToWorldCoordinate(snowball.getTargetY()), trajectory));

        gamePlayer.getSnowStormAttributes().getLastThrow().set(System.currentTimeMillis());
        snowball.scheduleMovementTask();

        /*GameScheduler.getInstance().getService().schedule(() -> {
            try {
                snowStormGame.handleSnowballLanding(snowball);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, snowball.getTimeToLive() * 100, TimeUnit.MILLISECONDS);*/
    }
}
