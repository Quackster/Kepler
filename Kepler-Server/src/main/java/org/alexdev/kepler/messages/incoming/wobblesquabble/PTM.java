package org.alexdev.kepler.messages.incoming.wobblesquabble;

import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabbleManager;
import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabbleMove;
import org.alexdev.kepler.game.games.wobblesquabble.WobbleSquabblePlayer;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

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
