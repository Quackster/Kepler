package net.h4bbo.kepler.game.games.battleball.events;

import net.h4bbo.kepler.game.games.GameEvent;
import net.h4bbo.kepler.game.games.battleball.BattleBallPowerUp;
import net.h4bbo.kepler.game.games.battleball.objects.PowerObject;
import net.h4bbo.kepler.game.games.enums.GameEventType;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class PowerUpSpawnEvent extends GameEvent {
    private final BattleBallPowerUp powerUp;

    public PowerUpSpawnEvent(BattleBallPowerUp powerUp) {
        super(GameEventType.BATTLEBALL_OBJECT_SPAWN);
        this.powerUp = powerUp;
    }

    @Override
    public void serialiseEvent(NettyResponse response) {
        response.writeInt(1);
        new PowerObject(this.powerUp).serialiseObject(response);
    }
}
