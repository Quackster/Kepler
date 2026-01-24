package net.h4bbo.kepler.messages.incoming.rooms.settings;

import net.h4bbo.kepler.dao.mysql.RoomDao;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.RoomManager;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.StringUtil;

public class SETFLATINFO implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
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

        RoomDao.save(room);
    }
}
