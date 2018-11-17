package org.alexdev.kepler.game.games.battleball.events;

import org.alexdev.kepler.game.games.GameEvent;
import org.alexdev.kepler.game.games.battleball.BattleBallPowerUp;
import org.alexdev.kepler.game.games.enums.GameEventType;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class AcquirePowerUpEvent extends GameEvent {
    private final BattleBallPowerUp powerUp;
    private final GamePlayer gamePlayer;

    public AcquirePowerUpEvent(GamePlayer gamePlayer, BattleBallPowerUp powerUp) {
        super(GameEventType.BATTLEBALL_POWERUP_GET);
        this.gamePlayer = gamePlayer;
        this.powerUp = powerUp;
    }

    @Override
    public void serialiseEvent(NettyResponse response) {
        response.writeInt(this.gamePlayer.getObjectId());
        response.writeInt(this.powerUp.getId());
        response.writeInt(this.powerUp.getPowerType().getPowerUpId());
    }
}
