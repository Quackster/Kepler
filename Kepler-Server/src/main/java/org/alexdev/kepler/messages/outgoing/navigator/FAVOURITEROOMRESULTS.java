package org.alexdev.kepler.messages.outgoing.navigator;

import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class FAVOURITEROOMRESULTS extends MessageComposer {
    private final int nodeType;
    private final List<Room> favouritePublicRooms;
    private final List<Room> favouriteFlatRooms;
    private final Player viewer;

    public FAVOURITEROOMRESULTS(Player viewer, List<Room> favouritePublicRooms, List<Room> favouriteFlatRooms) {
        this.nodeType = 2;
        this.viewer = viewer;
        this.favouritePublicRooms = favouritePublicRooms;
        this.favouriteFlatRooms = favouriteFlatRooms;
    }

    @Override
    public void compose(NettyResponse response) {
        /*  tNodeMask = tConn.GetIntFrom()
  tNodeId = tConn.GetIntFrom()
  tNodeType = tConn.GetIntFrom()
  tNodeInfo = [#id:string(tNodeId), #nodeType:tNodeType, #name:tConn.GetStrFrom(), #usercount:tConn.GetIntFrom(), #maxUsers:tConn.GetIntFrom(), #parentid:string(tConn.GetIntFrom())]*/
        response.writeInt(0);
        response.writeInt(0);
        response.writeInt(this.nodeType); // Node type: 2 to show private rooms
        response.writeString("");
        response.writeInt(0);
        response.writeInt(0);
        response.writeInt(0);

        if (this.nodeType == 2) {
            response.writeInt(this.favouriteFlatRooms.size());

            for (Room room : this.favouriteFlatRooms) {
                response.writeInt(room.getId());
                response.writeString(room.getData().getName());

                if (room.isOwner(this.viewer.getDetails().getId())|| room.getData().showOwnerName() || this.viewer.hasFuse(Fuseright.SEE_ALL_ROOMOWNERS)) {
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

        for (Room room : this.favouritePublicRooms) {
            int door = 0;
            String description = room.getData().getDescription();

            if (room.getData().getDescription().contains("/")) {
                String[] data =  description.split("/");
                description = data[0];
                door = Integer.parseInt(data[1]);
            }

            response.writeInt(room.getId() + RoomManager.PUBLIC_ROOM_OFFSET);
            response.writeInt(1);
            response.writeString(room.getData().getName());
            response.writeInt(room.getData().getTotalVisitorsNow());
            response.writeInt(room.getData().getTotalVisitorsMax());
            response.writeInt(room.getData().getCategoryId());
            response.writeString(description);
            response.writeInt(room.getId());
            response.writeInt(door);
            response.writeString(room.getData().getCcts());
            response.writeInt(0);
            response.writeInt(1);
        }
    }

    @Override
    public short getHeader() {
        return 61;
    }
}
