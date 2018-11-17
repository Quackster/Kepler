package org.alexdev.kepler.game.games.battleball.events;

import org.alexdev.kepler.game.games.GameEvent;
import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.enums.GameEventType;
import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class PlayerMoveEvent extends GameEvent {
    private final GamePlayer gamePlayer;
    private final Position nextPosition;

    public PlayerMoveEvent(GamePlayer gamePlayer, Position nextPosition) {
        super(GameEventType.BATTLEBALL_PLAYER_EVENT);
        this.gamePlayer = gamePlayer;
        this.nextPosition = nextPosition;
    }

    @Override
    public void serialiseEvent(NettyResponse response) {
        response.writeInt(this.gamePlayer.getPlayer().getRoomUser().getInstanceId());
        response.writeInt(this.nextPosition.getX());
        response.writeInt(this.nextPosition.getY());
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }
}
