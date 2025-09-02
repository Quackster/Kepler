package net.h4bbo.kepler.messages.incoming.moderation;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.moderation.cfh.CallForHelp;
import net.h4bbo.kepler.game.moderation.cfh.CallForHelpManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
