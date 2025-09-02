package net.h4bbo.kepler.messages.incoming.moderation;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.moderation.cfh.CallForHelp;
import net.h4bbo.kepler.game.moderation.cfh.CallForHelpManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.outgoing.moderation.CRY_REPLY;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class MESSAGETOCALLER implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // Only players that have this fuse are allowed to reply to call for helps
        if (!player.hasFuse(Fuseright.RECEIVE_CALLS_FOR_HELP)) {
            return;
        }

        // The inconsistent v21 client sends the call ID non-VL64 encoded :/
        int callId = Integer.parseInt(reader.readString());
        String message = reader.readString();

        // Retrieve call for help by ID provided by client
        CallForHelp cfh = CallForHelpManager.getInstance().getCall(callId);

        // Make sure call for help exists
        // TODO: find error packet / message to return to client
        if (cfh == null) {
            return;
        }

        // Call has been handled, delete it :)
        CallForHelpManager.getInstance().deleteCall(cfh);

        // Get callee of call for help
        Player caller = PlayerManager.getInstance().getPlayerById(cfh.getCaller());

        // Make sure caller isn't null
        if (caller == null) {
            return;
        }

        // Notify callee
        caller.send(new CRY_REPLY(message));
    }
}
