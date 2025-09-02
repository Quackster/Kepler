package net.h4bbo.kepler.messages.incoming.moderation;

import net.h4bbo.kepler.game.moderation.cfh.CallForHelp;
import net.h4bbo.kepler.game.moderation.cfh.CallForHelpManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.moderation.CFH_ACK;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class REQUEST_CFH implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // Retrieve open calls from the current user
        CallForHelp call = CallForHelpManager.getInstance().getPendingCall(player.getDetails().getId());

        // Send details
        player.send(new CFH_ACK(call));
    }
}
