package net.h4bbo.kepler.messages.outgoing.navigator;

import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.navigator.NavigatorCategory;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class NAVNODEINFO extends MessageComposer {
    private Player viewer;
    private NavigatorCategory parentCategory;
    private List<Room> rooms;
    private boolean hideFull;
    private List<NavigatorCategory> subCategories;
    private int categoryCurrentVisitors;
    private int categoryMaxVisitors;
    private int rank;

    public NAVNODEINFO(Player viewer, NavigatorCategory parentCategory, List<Room> rooms, boolean hideFull, List<NavigatorCategory> subCategories, int categoryCurrentVisitors, int categoryMaxVisitors, int rank) {
        this.viewer = viewer;
        this.parentCategory = parentCategory;
        this.rooms = rooms;
        this.hideFull = hideFull;
        this.subCategories = subCategories;
        this.categoryCurrentVisitors = categoryCurrentVisitors;
        this.categoryMaxVisitors = categoryMaxVisitors;
        this.rank = rank;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeBool(this.hideFull);
        response.writeInt(this.parentCategory.getId());
        response.writeInt(this.parentCategory.isPublicSpaces() ? 0 : 2);
        response.writeString(this.parentCategory.getName());
        response.writeInt(this.categoryCurrentVisitors);
        response.writeInt(this.categoryMaxVisitors);
        response.writeInt(this.parentCategory.getParentId());

        if (!this.parentCategory.isPublicSpaces()) {
            response.writeInt(this.rooms.size());
        }

        for (Room room : this.rooms) {
            if (room.isPublicRoom()) {
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
            } else {
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

        for (NavigatorCategory subCategory : this.subCategories) {
            if (subCategory.getMinimumRoleAccess().getRankId() > this.rank) {
                continue;
            }
            response.writeInt(subCategory.getId());
            response.writeInt(0);
            response.writeString(subCategory.getName());
            response.writeInt(subCategory.getCurrentVisitors());
            response.writeInt(subCategory.getMaxVisitors());
            response.writeInt(this.parentCategory.getId());
        }

    }

    @Override
    public short getHeader() {
        return 220; // "C\"
    }
}
