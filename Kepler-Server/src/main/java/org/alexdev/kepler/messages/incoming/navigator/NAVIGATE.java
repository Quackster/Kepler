package org.alexdev.kepler.messages.incoming.navigator;

import org.alexdev.kepler.dao.mysql.NavigatorDao;
import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.navigator.NavigatorCategory;
import org.alexdev.kepler.game.navigator.NavigatorManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.navigator.NAVNODEINFO;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NAVIGATE implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        boolean hideFull = reader.readInt() == 1;
        int categoryId = reader.readInt();

        if (categoryId >= RoomManager.PUBLIC_ROOM_OFFSET) { // Public room follow, there should not any categories with an ID of 1000 or over... lol
            Room room = RoomManager.getInstance().getRoomById(categoryId - RoomManager.PUBLIC_ROOM_OFFSET);

            if (room != null) {
                categoryId = room.getCategory().getId();
            }
        }

        NavigatorCategory category = NavigatorManager.getInstance().getCategoryById(categoryId);

        if (category == null) {
            return;
        }

        if (category.getMinimumRoleAccess().getRankId() > player.getDetails().getRank().getRankId()) {
            return;
        }

        List<NavigatorCategory> subCategories = NavigatorManager.getInstance().getCategoriesByParentId(category.getId());
        subCategories.sort(Comparator.comparingDouble(NavigatorCategory::getCurrentVisitors).reversed());

        List<Room> rooms = new ArrayList<>();

        int categoryCurrentVisitors = category.getCurrentVisitors();
        int categoryMaxVisitors = category.getMaxVisitors();

        if (category.isPublicSpaces()) {
            for (Room room : RoomManager.getInstance().replaceQueryRooms(RoomDao.getRoomsByUserId(0))) {
                if (room.getData().isNavigatorHide()) {
                    continue;
                }

                if (room.getData().getCategoryId() != category.getId()) {
                    continue;
                }

                if (hideFull && (room.getData().getVisitorsNow() >= room.getData().getVisitorsMax())) {
                    continue;
                }

                rooms.add(room);
            }
        } else {
            for (Room room : RoomManager.getInstance().replaceQueryRooms(NavigatorDao.getRecentRooms(30, category.getId()))) {
                if (room.getData().getCategoryId() != category.getId()) {
                    continue;
                }

                if (hideFull && (room.getData().getVisitorsNow() >= room.getData().getVisitorsMax())) {
                    continue;
                }

                rooms.add(room);
            }
        }

        RoomManager.getInstance().sortRooms(rooms);
        player.send(new NAVNODEINFO(player, category, rooms, hideFull, subCategories, categoryCurrentVisitors, categoryMaxVisitors, player.getDetails().getRank().getRankId()));

    }
}
