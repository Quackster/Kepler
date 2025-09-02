package net.h4bbo.kepler.messages.outgoing.navigator;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class RECOMMENDED_ROOM_LIST extends MessageComposer {
    private final Player player;
    private final List<Room> roomList;

    public RECOMMENDED_ROOM_LIST(Player player, List<Room> roomList) {
        this.player = player;
        this.roomList = roomList;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.roomList.size());

        for (Room room : this.roomList) {
            response.writeInt(room.getId());
            response.writeString(room.getData().getName());

            if (room.isOwner(this.player.getDetails().getId()) || room.getData().showOwnerName() || this.player.hasFuse(Fuseright.SEE_ALL_ROOMOWNERS)) {
                response.writeString(room.getData().getOwnerName());
            } else {
                response.writeString("-");
            }

            response.writeString(room.getData().getAccessType());
            response.writeInt(room.getData().getVisitorsNow());
            response.writeInt(room.getData().getVisitorsMax());
            response.writeString(room.getData().getDescription());
        }
    }

    @Override
    public short getHeader() {
        return 351; // "E_"
    }
}
