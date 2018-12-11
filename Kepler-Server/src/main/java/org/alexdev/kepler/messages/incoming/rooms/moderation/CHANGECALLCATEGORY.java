package org.alexdev.kepler.messages.incoming.rooms.moderation;

import org.alexdev.kepler.game.moderation.CallForHelp;
import org.alexdev.kepler.game.moderation.CallForHelpManager;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class CHANGECALLCATEGORY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // Only players that have this fuse are allowed to change call category
        if (!player.hasFuse(Fuseright.RECEIVE_CALLS_FOR_HELP)) {
            return;
        }

        int callId = Integer.parseInt(reader.readString());
        int category = reader.readInt();

        CallForHelp cfh = CallForHelpManager.getInstance().getCall(callId);
        CallForHelpManager.getInstance().changeCategory(cfh, category);
    }
}
