package org.alexdev.kepler.game.commandqueue.commands;

import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.commandqueue.CommandTemplate;
import org.alexdev.kepler.game.navigator.NavigatorManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.messenger.ROOMFORWARD;

public class RoomUpdateCommand implements Command {
    public void executeCommand(CommandTemplate commandArgs) {
        var room = RoomDao.getRoomById(commandArgs.RoomId);
        if(room != null) {

            System.out.println(room.getData().getId());
            System.out.println(room.getData().getVisitorsMax());
            System.out.println(room.getData().getAccessType());


            /*
            *
        String contents = reader.contents();

        if (contents.startsWith("/")) {
            contents = contents.substring(1);
        }

        int roomId = Integer.parseInt(contents.split("/")[0]);

        Room room = RoomManager.getInstance().getRoomById(roomId);

        if (room == null) {
            return;
        }

        if (!room.isOwner(player.getDetails().getId())) {
            return;
        }

        String[] data = contents.split(Character.toString((char) 13));

        for (String setting : data) {
            int index = setting.indexOf("=");

            if (index == -1) {
                continue;
            }

            String key = setting.substring(0, index);
            String value = setting.substring(index + 1);

            if (key.startsWith("description")) {
                room.getData().setDescription(StringUtil.filterInput(value, true));
            }

            if (key.startsWith("allsuperuser")) {
                room.getData().setSuperUsers(Integer.parseInt(value) == 1);
            }

            if (key.startsWith("maxvisitors")) {
                int maxVisitors = Integer.parseInt(value);

                if (maxVisitors < 10 || maxVisitors > 50) {
                    maxVisitors = 25;
                }

                room.getData().setVisitorsMax(maxVisitors);
            }

            if (key.startsWith("password")) {
                room.getData().setPassword(StringUtil.filterInput(value, true));
            }
        }

        RoomDao.save(room);*/
            room.getData().setName(commandArgs.RoomName);
            room.getData().setDescription(commandArgs.RoomDescription);
            room.getData().setShowOwnerName(commandArgs.RoomShowOwnerName);
            room.getData().setAccessType(commandArgs.RoomAccessType);

            RoomDao.save(room);

            NavigatorManager.getInstance().resetCategoryMap();
        }
    }
}
