package org.alexdev.kepler.game.games.snowstorm.events;

import org.alexdev.kepler.game.games.enums.GameObjectType;
import org.alexdev.kepler.game.games.snowstorm.SnowStormObject;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class SnowStormAvatarMoveEvent extends SnowStormObject {
    private final Player player;
    private int X;
    private int Y;

    public SnowStormAvatarMoveEvent(Player player, int x, int y) {
        super(GameObjectType.SNOWWAR_AVATAR_MOVE_EVENT);
        this.player = player;
        this.X = x;
        this.Y = y;
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(GameObjectType.SNOWWAR_AVATAR_MOVE_EVENT.getObjectId());
        response.writeInt(player.getDetails().getId());
        response.writeInt(this.X);
        response.writeInt(this.Y);
    }
}
