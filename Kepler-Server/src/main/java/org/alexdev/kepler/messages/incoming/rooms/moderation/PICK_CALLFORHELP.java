package org.alexdev.kepler.messages.incoming.rooms.moderation;

import org.alexdev.kepler.game.moderation.CallForHelp;
import org.alexdev.kepler.game.moderation.CallForHelpManager;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class PICK_CALLFORHELP implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // Only players that have this fuse are allowed to pick up calls for help
        if (!player.hasFuse(Fuseright.RECEIVE_CALLS_FOR_HELP)) {
            return;
        }

        // The inconsistent v21 client sends the call ID non-VL64 encoded :/
        int callId = Integer.parseInt(reader.readString());

        // Retrieve call for help by ID provided by client
        CallForHelp cfh = CallForHelpManager.getInstance().getCall(callId);

        // Make sure call for help exists
        // TODO: find error packet / message to return to client
        if (cfh ==  null) {
            return;
        }

        // Pick up call for help
        CallForHelpManager.getInstance().pickUp(cfh, player);
    }
}
