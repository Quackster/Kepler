package org.alexdev.kepler.game.games.battleball.objects;

import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class PlayerObject extends GameObject {
    private final GamePlayer gamePlayer;

    public PlayerObject(GamePlayer gamePlayer) {
        super(gamePlayer.getObjectId(), GameObjectType.BATTLEBALL_PLAYER_OBJECT);
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(this.gamePlayer.getObjectId());
        response.writeInt(this.gamePlayer.getPlayer().getRoomUser().getPosition().getX());
        response.writeInt(this.gamePlayer.getPlayer().getRoomUser().getPosition().getY());
        response.writeInt((int) this.gamePlayer.getPlayer().getRoomUser().getPosition().getZ());
        response.writeInt(this.gamePlayer.getPlayer().getRoomUser().getPosition().getRotation());
        response.writeInt(this.gamePlayer.getPlayerState().getStateId());
        response.writeInt(this.gamePlayer.getColouringForOpponentId());
        response.writeString(this.gamePlayer.getPlayer().getDetails().getName());
        response.writeString(this.gamePlayer.getPlayer().getDetails().getMotto());
        response.writeString(this.gamePlayer.getPlayer().getDetails().getFigure());
        response.writeString(this.gamePlayer.getPlayer().getDetails().getSex());
        response.writeInt(this.gamePlayer.getTeamId());
        response.writeInt(this.gamePlayer.getObjectId());
    }
}
