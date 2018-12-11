package org.alexdev.kepler.messages.incoming.rooms.moderation;

import org.alexdev.kepler.game.moderation.CallForHelp;
import org.alexdev.kepler.game.moderation.CallForHelpManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.CFH_ACK;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class REQUEST_CFH implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // Retrieve open calls from the current user
        CallForHelp call = CallForHelpManager.getInstance().getPendingCall(player.getDetails().getId());

        // Send details
        player.send(new CFH_ACK(call));
    }
}
