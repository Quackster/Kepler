package org.alexdev.kepler.game.games.snowstorm.util;

import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public interface SnowStormMessage {
    void handle(NettyRequest request, SnowStormGame snowStormGame, GamePlayer gamePlayer);
}
