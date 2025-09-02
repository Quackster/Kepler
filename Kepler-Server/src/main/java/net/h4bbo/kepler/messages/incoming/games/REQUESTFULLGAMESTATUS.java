package net.h4bbo.kepler.messages.incoming.games;

import net.h4bbo.kepler.game.games.Game;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.games.FULLGAMESTATUS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class REQUESTFULLGAMESTATUS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

        if (gamePlayer == null) {
            return;
        }

        Game game = GameManager.getInstance().getGameById(gamePlayer.getGameId());

        if (!(game instanceof SnowStormGame)) {
            return;
        }

        player.send(new FULLGAMESTATUS(game));

        //player.send(new SNOWSTORM_FULLGAMESTATUS((SnowStormGame) game, gamePlayer, objects, snowStormGame.getExecutingEvents()));
    }
}
