package org.alexdev.kepler.messages.incoming.moderation;

import org.alexdev.kepler.game.moderation.cfh.CallForHelp;
import org.alexdev.kepler.game.moderation.cfh.CallForHelpManager;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

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
        //CallForHelpManager.getInstance().deleteCall(cfh);
    }
}