package org.alexdev.kepler.game.room.handlers;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.dao.mysql.NavigatorDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.room.Room;

import java.sql.SQLException;

public class RoomSelectionHandler {
    public static boolean selectRoom(int userId, int roomType) throws SQLException {
        PlayerDetails playerDetails = PlayerDao.getDetails(userId);

        if (playerDetails == null) {
            return false;
        }

        if (roomType < 0 || roomType > 5) {
            return false;
        }

        int stoolX = -1;
        int stoolY = -1;
        int stoolRotation = -1;

        int floor = -1;
        int wallpaper = -1;

        switch (roomType) {
            case 0:
            {
                stoolX = 1;
                stoolY = 6;
                stoolRotation = 2;

                floor = 601;
                wallpaper = 1501;
                break;
            }

            case 1:
            {
                stoolX = 3;
                stoolY = 6;
                stoolRotation = 4;

                wallpaper = 607;
                floor = 0;
                break;
            }

            case 2:
            {
                stoolX = 2;
                stoolY = 2;
                stoolRotation = 4;

                wallpaper = 1901;
                floor = 301;
                break;
            }


            case 3:
            {
                stoolX = 1;
                stoolY = 2;
                stoolRotation = 2;

                wallpaper = 1801;
                floor = 110;
                break;
            }


            case 4:
            {
                stoolX = 3;
                stoolY = 6;
                stoolRotation = 0;

                wallpaper = 503;
                floor = 104;
                break;
            }

            case 5:
            {
                stoolX = 3;
                stoolY = 6;
                stoolRotation = 0;

                wallpaper = 804;//107;
                floor = 107;//804;
                break;
            }
        }

            int roomId = NavigatorDao.createRoom(userId, playerDetails.getName() + "'s Room", "model_s", true, 0);
            Room room = RoomDao.getRoomById(roomId);

            if (room == null) {
                return false;
            }

            room.getData().setWallpaper(wallpaper);
            room.getData().setFloor(floor);
            RoomDao.saveDecorations(room);

            room.getData().setDescription(playerDetails.getName() + " has entered the building");
            RoomDao.save(room);

            Item item = new Item();
            item.setDefinitionId(ItemManager.getInstance().getDefinitionBySprite("noob_stool*" + (roomType + 1)).getId());
            item.setOwnerId(userId);
            item.getPosition().setX(stoolX);
            item.getPosition().setY(stoolY);
            item.getPosition().setRotation(stoolRotation);
            item.setRoomId(roomId);
            ItemDao.newItem(item);
            ItemDao.updateItem(item);

            Item table = new Item();
            table.setDefinitionId(ItemManager.getInstance().getDefinitionBySprite("noob_table*" + (roomType + 1)).getId());
            table.setOwnerId(userId);
            ItemDao.newItem(table);

            Item window = new Item();
            window.setWallPosition(":w=3,0 l=13,71 r");
            window.setDefinitionId(ItemManager.getInstance().getDefinitionBySprite("noob_window_double").getId());
            window.setOwnerId(userId);
            window.setRoomId(roomId);
            ItemDao.newItem(window);
            ItemDao.updateItem(window);

            PlayerDao.saveSelectedRoom(userId, roomId);
            return true;
    }
}
