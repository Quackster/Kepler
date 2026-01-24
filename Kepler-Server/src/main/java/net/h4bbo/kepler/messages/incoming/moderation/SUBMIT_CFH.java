package net.h4bbo.kepler.messages.incoming.moderation;

import net.h4bbo.kepler.game.moderation.cfh.CallForHelpManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
