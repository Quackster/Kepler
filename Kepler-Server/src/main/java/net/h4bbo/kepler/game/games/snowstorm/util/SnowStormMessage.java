package net.h4bbo.kepler.game.games.snowstorm.util;

import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public interface SnowStormMessage {
    void handle(NettyRequest request, SnowStormGame snowStormGame, GamePlayer gamePlayer);
}
