package net.h4bbo.kepler.game.games.battleball.objects;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.battleball.BattleBallGame;
import net.h4bbo.kepler.game.games.battleball.BattleBallPlayerStateManager;
import net.h4bbo.kepler.game.games.battleball.enums.BattleBallPlayerState;
import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class PlayerUpdateObject extends GameObject {
    private final GamePlayer gamePlayer;

    public PlayerUpdateObject(GamePlayer gamePlayer) {
        super(gamePlayer.getPlayer().getDetails().getId(), GameObjectType.BATTLEBALL_PLAYER_OBJECT);
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(this.gamePlayer.getObjectId());
        response.writeInt(this.gamePlayer.getPlayer().getRoomUser().getPosition().getX());
        response.writeInt(this.gamePlayer.getPlayer().getRoomUser().getPosition().getY());
        response.writeInt((int) this.gamePlayer.getPlayer().getRoomUser().getPosition().getZ());
        response.writeInt(this.gamePlayer.getPlayer().getRoomUser().getPosition().getRotation());
        response.writeInt(getPlayerState().getStateId());
        BattleBallGame battleBallGame = (BattleBallGame) this.gamePlayer.getGame();
        response.writeInt(battleBallGame.getColouringForOpponentId(this.gamePlayer));
    }

    private BattleBallPlayerState getPlayerState() {
        var game = this.gamePlayer.getGame();
        if (game instanceof BattleBallGame battleBallGame) {
            BattleBallPlayerStateManager manager = battleBallGame.getPlayerStateManager();
            return manager.getState(this.gamePlayer);
        }

        return BattleBallPlayerState.NORMAL;
    }
}
