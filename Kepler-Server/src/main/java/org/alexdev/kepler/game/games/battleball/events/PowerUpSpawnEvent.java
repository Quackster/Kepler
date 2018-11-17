package org.alexdev.kepler.game.games.battleball.events;

import org.alexdev.kepler.game.games.GameEvent;
import org.alexdev.kepler.game.games.battleball.BattleBallPowerUp;
import org.alexdev.kepler.game.games.battleball.objects.PowerObject;
import org.alexdev.kepler.game.games.enums.GameEventType;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

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
