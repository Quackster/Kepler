package org.alexdev.kepler.messages.incoming.rooms.moderation;

import org.alexdev.kepler.game.moderation.CallForHelp;
import org.alexdev.kepler.game.moderation.CallForHelpManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.CFH_ACK;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

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
