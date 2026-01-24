package net.h4bbo.kepler.messages.incoming.wobblesquabble;

import net.h4bbo.kepler.game.games.wobblesquabble.WobbleSquabbleManager;
import net.h4bbo.kepler.game.games.wobblesquabble.WobbleSquabbleMove;
import net.h4bbo.kepler.game.games.wobblesquabble.WobbleSquabblePlayer;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class PTM implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!WobbleSquabbleManager.getInstance().isPlaying(player)) {
            return;
        }

        var move = WobbleSquabbleMove.getMove(reader.contents());

        if (move == null) {
            return;
        }

        WobbleSquabblePlayer wsPlayer = WobbleSquabbleManager.getInstance().getPlayer(player);

        if (wsPlayer == null) {
            return;
        }

        wsPlayer.setMove(move);
        wsPlayer.setRequiresUpdate(true);
    }
}
