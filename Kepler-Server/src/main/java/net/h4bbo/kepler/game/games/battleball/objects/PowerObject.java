package net.h4bbo.kepler.game.games.battleball.objects;

import net.h4bbo.kepler.game.games.GameObject;
import net.h4bbo.kepler.game.games.battleball.BattleBallPowerUp;
import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class PowerObject extends GameObject {
    private final BattleBallPowerUp powerUp;

    public PowerObject(BattleBallPowerUp powerUp) {
        super(powerUp.getId(), GameObjectType.BATTLEBALL_POWER_OBJECT);
        this.powerUp = powerUp;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(this.powerUp.getId());
        response.writeInt(this.powerUp.getTimeToDespawn().get());
        response.writeInt(this.powerUp.getPlayerHolding());
        response.writeInt(this.powerUp.getPowerType().getPowerUpId());
        response.writeInt(this.powerUp.getTile().getPosition().getX());
        response.writeInt(this.powerUp.getTile().getPosition().getY());
        response.writeInt((int) this.powerUp.getTile().getPosition().getZ());
    }
}
