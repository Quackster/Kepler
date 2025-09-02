package net.h4bbo.kepler.messages.incoming.rooms;

import net.h4bbo.kepler.game.games.player.GamePlayer;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormGame;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.games.FULLGAMESTATUS;
import net.h4bbo.kepler.messages.outgoing.rooms.HEIGHTMAP;
import net.h4bbo.kepler.messages.outgoing.rooms.OBJECTS_WORLD;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
