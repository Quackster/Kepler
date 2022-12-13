package org.alexdev.kepler.messages.incoming.moderation;

import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.moderation.cfh.CallForHelp;
import org.alexdev.kepler.game.moderation.cfh.CallForHelpManager;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class CHANGECALLCATEGORY implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // Only players that have this fuse are allowed to change call category
        if (!player.hasFuse(Fuse.RECEIVE_CALLS_FOR_HELP)) {
            return;
        }

        int callId = Integer.parseInt(reader.readString());
        int category = reader.readInt();

        CallForHelp cfh = CallForHelpManager.getInstance().getCall(callId);
        CallForHelpManager.getInstance().changeCategory(cfh, category);
    }
}
