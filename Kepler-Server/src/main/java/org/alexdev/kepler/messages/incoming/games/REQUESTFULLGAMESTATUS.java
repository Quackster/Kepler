package org.alexdev.kepler.messages.incoming.games;

import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.GameObject;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.games.SNOWSTORM_FULLGAMESTATUS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

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

        SnowStormGame snowStormGame = (SnowStormGame) game;
        List<GameObject> objects = game.getObjects();

        gamePlayer.getTurnContainer().iterateTurn();
        gamePlayer.getTurnContainer().calculateChecksum(objects);

        player.send(new SNOWSTORM_FULLGAMESTATUS((SnowStormGame) game, gamePlayer, objects, snowStormGame.getExecutingEvents()));
    }
}
