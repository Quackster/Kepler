package net.h4bbo.kepler.messages.outgoing.messenger;

import net.h4bbo.kepler.game.messenger.MessengerMessage;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;
import net.h4bbo.kepler.util.DateUtil;

public class MESSENGER_MSG extends MessageComposer {
    private final MessengerMessage message;

    public MESSENGER_MSG(MessengerMessage message) {
        this.message = message;
    }

    @Override
    public void compose(NettyResponse response) {
        /*
          if (getPlayer().getVersion() < 23) {   */
            //if (getPlayer().getVersion() <= 14) {
                response.writeInt(1);
            //}//



            response.writeInt(this.message.getId());
            response.writeInt(this.message.getFromId());
            response.writeString(DateUtil.getDateAsString(this.message.getTimeSet()));
            response.writeString(this.message.getMessage());
        /*} else {
            //response.writeInt(this.message.getVirtualId());
            response.writeInt(this.message.getFromId());
            //response.writeString(DateUtil.getDateAsString(this.message.getTimeSet()));
            response.writeString(this.message.getMessage());
        }*/
    }

    @Override
    public short getHeader() {
        return 134; // "BF"
    }
}
