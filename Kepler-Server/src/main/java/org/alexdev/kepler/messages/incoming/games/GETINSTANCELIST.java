package org.alexdev.kepler.messages.incoming.games;

import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GameLobbyTrigger;
import org.alexdev.kepler.messages.outgoing.games.INSTANCELIST;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.config.GameConfiguration;

import java.util.stream.Collectors;

public class GETINSTANCELIST implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        if (!(room.getModel().getModelTrigger() instanceof GameLobbyTrigger)) {
            return;
        }

        GameLobbyTrigger gameLobbyTrigger = (GameLobbyTrigger) room.getModel().getModelTrigger();

        // Don't show panel and lounge info if create game is disabled
        if (!GameConfiguration.getInstance().getBoolean(gameLobbyTrigger.getGameType().name().toLowerCase() + ".create.game.enabled")) {
            return;
        }

        player.send(gameLobbyTrigger.getInstanceList());
    }
}
