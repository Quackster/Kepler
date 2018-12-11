package org.alexdev.kepler.messages.incoming.rooms.moderation;

import org.alexdev.kepler.game.moderation.CallForHelpManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SUBMIT_CFH implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        String message = reader.readString();

        if (message.length() == 0) {
            return;
        }

        // TODO: ignore messages that only contains spaces

        // Only allow one call for help per user
        if (CallForHelpManager.getInstance().hasPendingCall(player)) {
            return;
        }

        CallForHelpManager.getInstance().submitCall(player, message);
    }
}
