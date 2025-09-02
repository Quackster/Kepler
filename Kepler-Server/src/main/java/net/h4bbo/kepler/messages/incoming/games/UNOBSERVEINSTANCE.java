package net.h4bbo.kepler.messages.incoming.games;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class UNOBSERVEINSTANCE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getObservingGameId() != -1) {
            player.getRoomUser().stopObservingGame();
        }

        /*
        if (player.getRoomUser().getGamePlayer() != null) {
            GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();
            Game game = gamePlayer.getGame();

            if (game != null) {
                if (gamePlayer.getGame().getSpectators().contains(gamePlayer)) {
                    gamePlayer.getGame().leaveGame(gamePlayer);
                }
            }
        }
        */
    }
}
