package org.alexdev.kepler.messages.outgoing.moderation;

import org.alexdev.kepler.game.moderation.CallForHelp;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class CALL_FOR_HELP extends MessageComposer {
    private CallForHelp cfh;

    public CALL_FOR_HELP(CallForHelp cfh){
        this.cfh = cfh;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.cfh.getCallId());
        response.writeInt(this.cfh.getCategory());
        response.writeString(this.cfh.getFormattedRequestTime());
        response.writeString(this.cfh.getCaller());
        response.writeString(this.cfh.getMessage());
        response.writeString(this.cfh.getCaller());
        response.writeString(this.cfh.getRoomName());
        response.writeInt(1); // TODO: find out what this is
        response.writeString("Marker"); // TODO: find out what this is
        response.writeInt(this.cfh.getRoomId());
        response.writeString(this.cfh.getRoomOwner());
    }

    @Override
    public short getHeader() {
        return 148; // "BT"
    }
}
