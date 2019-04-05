package org.alexdev.kepler.messages.incoming.moderation;

import org.alexdev.kepler.game.moderation.cfh.CallForHelp;
import org.alexdev.kepler.game.moderation.cfh.CallForHelpManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.moderation.CFH_ACK;
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
