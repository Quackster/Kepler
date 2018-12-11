package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.game.moderation.CallForHelp;
import org.alexdev.kepler.game.moderation.CallForHelpManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;
import org.alexdev.kepler.util.encoding.Base64Encoding;
import org.alexdev.kepler.util.encoding.VL64Encoding;

public class CFH_ACK extends MessageComposer {
    private final CallForHelp call;

    public CFH_ACK(CallForHelp call){
        this.call = call;
    }

    @Override
    public void compose(NettyResponse response) {
        // TODO: verify if structure and packet name is correct by looking at the lingo
        response.writeBool(call != null);

        if (call != null) {
            response.writeString(call.getCallId());
            response.writeString(call.getFormattedRequestTime());
            response.writeString(call.getMessage());
        }
    }

    @Override
    public short getHeader() {
        return 319; // "D"
    }
}
