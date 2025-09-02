package net.h4bbo.kepler.messages.outgoing.moderation;

import net.h4bbo.kepler.game.moderation.cfh.CallForHelp;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class CALL_FOR_HELP extends MessageComposer {
    private CallForHelp cfh;

    public CALL_FOR_HELP(CallForHelp cfh){
        this.cfh = cfh;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.cfh.getCryId());
        response.writeInt(this.cfh.getCategory());
        response.writeString(this.cfh.getFormattedRequestTime());
        response.writeString(this.cfh.getCaller());
        response.writeString(this.cfh.getMessage());
        response.writeString(this.cfh.getCaller());
        response.writeString(this.cfh.getRoom().getData().getName());

        if (this.cfh.getRoom() != null) {
            if (this.cfh.getRoom().isPublicRoom()) {
                response.writeInt(0);
                response.writeString(this.cfh.getRoom().getData().getCcts());
                response.writeInt(this.cfh.getRoom().getId() + RoomManager.PUBLIC_ROOM_OFFSET);
                response.writeInt(this.cfh.getRoom().getId());
            } else {
                response.writeInt(1);
                response.writeString(this.cfh.getRoom().getData().getName());
                response.writeInt(this.cfh.getRoom().getId() );
                response.writeString(this.cfh.getRoom().getData().getOwnerName());
            }
        }
    }

    @Override
    public short getHeader() {
        return 148; // "BT"
    }
}
