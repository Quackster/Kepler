package net.h4bbo.kepler.messages.incoming.moderation;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.moderation.cfh.CallForHelp;
import net.h4bbo.kepler.game.moderation.cfh.CallForHelpManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class PICK_CALLFORHELP implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!player.hasFuse(Fuseright.RECEIVE_CALLS_FOR_HELP)) {
            return;
        }

        int callId = Integer.parseInt(reader.readString());
        CallForHelp cfh = CallForHelpManager.getInstance().getCall(callId);

        if (cfh == null) {
            return;
        }

        CallForHelpManager.getInstance().pickUp(cfh, player);
        CallForHelpManager.getInstance().deleteCall(cfh);
    }
}