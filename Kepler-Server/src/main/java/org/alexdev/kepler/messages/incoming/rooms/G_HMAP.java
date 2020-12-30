package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.snowstorm.SnowStormGame;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.games.FULLGAMESTATUS;
import org.alexdev.kepler.messages.outgoing.rooms.HEIGHTMAP;
import org.alexdev.kepler.messages.outgoing.rooms.OBJECTS_WORLD;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;

public class G_HMAP implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        GamePlayer gamePlayer = player.getRoomUser().getGamePlayer();

        if (gamePlayer != null && gamePlayer.getGame() instanceof SnowStormGame) {
            SnowStormGame game = (SnowStormGame) gamePlayer.getGame();
            player.send(new HEIGHTMAP(game.getMap().getHeightMap()));
        } else {
            player.send(new HEIGHTMAP(player.getRoomUser().getRoom().getModel()));
        }

        if (gamePlayer != null) {
            player.send(new FULLGAMESTATUS(gamePlayer.getGame()));

            if (gamePlayer.getGame() instanceof SnowStormGame) {
                SnowStormGame game = (SnowStormGame) gamePlayer.getGame();
                player.send(new OBJECTS_WORLD(game.getMap().getCompiledItems()));
            }

            return;
        }
    }
}
