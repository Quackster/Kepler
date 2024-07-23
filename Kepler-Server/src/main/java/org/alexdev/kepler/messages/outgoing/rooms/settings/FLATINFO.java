package org.alexdev.kepler.messages.outgoing.rooms.settings;

import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class FLATINFO extends MessageComposer {
    private Player player;
    private Room room;

    public FLATINFO(Player player, Room room) {
        this.player = player;
        this.room = room;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeBool(this.room.getData().allowSuperUsers()); // #ableothersmovefurniture
        response.writeInt(this.room.getData().getAccessTypeId()); // #door
        response.writeInt(this.room.getId()); // #flatId

        if (this.room.isOwner(player.getDetails().getId())|| this.room.getData().showOwnerName() || this.player.hasFuse(Fuse.SEE_ALL_ROOMOWNERS)) {
            response.writeString(this.room.getData().getOwnerName()); // #owner
        } else {
            response.writeString("-"); // #owner
        }

        response.writeString(this.room.getModel().getName()); // #marker
        response.writeString(this.room.getData().getName()); // #name
        response.writeString(this.room.getData().getDescription()); // #description
        response.writeBool(this.room.getData().showOwnerName()); // #showownername
        response.writeBool(this.room.getCategory().hasAllowTrading()); // #trading
        response.writeBool(false); // #alert
        /* Edited 23-07-2024 - WTF was Quackster thinking here?? Max visitors should be here not the current amount of visitors in the room.
        * This fucked up the client, if you were joining a room, then editing it the "max visitors" would be represented as 1.
        *
        *       "nav_maxusers_minus":
        * tMaxVisitors = integer(me.getComponent().getNodeProperty(tNodeId, #maxVisitors) - 5)
        * if tMaxVisitors < 10 then
        *    tMaxVisitors = 10
        * end if
        * getWindow(me.pWindowTitle).getElement("nav_maxusers_amount").setText(tMaxVisitors)
        * me.getComponent().setNodeProperty(tNodeId, #maxVisitors, tMaxVisitors)
        * "nav_maxusers_plus":
        * tAbsoluteMax = me.getComponent().getNodeProperty(tNodeId, #absoluteMaxVisitors)
        * tMaxVisitors = integer(me.getComponent().getNodeProperty(tNodeId, #maxVisitors) + 5)
        * if tMaxVisitors > tAbsoluteMax then
        *   tMaxVisitors = tAbsoluteMax
         * end if
        *
        * TODO Add the absolute max visitors to the room model data and use that instead hardcoding 200
        * */
        response.writeInt(this.room.getData().getVisitorsMax()); // #maxVisitors
        response.writeInt(200); // #absoluteMaxVisitors
    }

    @Override
    public short getHeader() {
        return 54; // "@v"
    }
}

