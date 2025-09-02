package net.h4bbo.kepler.messages.incoming.moderation;

import net.h4bbo.kepler.game.moderation.cfh.CallForHelp;
import net.h4bbo.kepler.game.moderation.cfh.CallForHelpManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.moderation.CFH_ACK;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class DELETE_CRY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // Retrieve open calls for current user
        CallForHelp cfh = CallForHelpManager.getInstance().getPendingCall(player.getDetails().getId());

        // Make sure call for help exists
        if (cfh == null) {
            return;
        }

        // Delete call for help
        CallForHelpManager.getInstance().deleteCall(cfh);

        // Notify client about the deleted call for help
        player.send(new CFH_ACK(null));
    }
}
