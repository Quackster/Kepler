package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.dao.mysql.RoomDao;
import org.alexdev.kepler.game.moderation.Fuseright;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.messages.outgoing.rooms.FLATNOTALLOWEDTOENTER;
import org.alexdev.kepler.messages.outgoing.rooms.DOORBELL_WAIT;
import org.alexdev.kepler.messages.outgoing.rooms.FLAT_LETIN;
import org.alexdev.kepler.messages.outgoing.user.LOCALISED_ERROR;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

public class TRYFLAT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int roomId = -1;

        String contents = reader.contents();
        String password = "";

        // Check if data has been sent by client
        if (contents.length() == 0) {
            return;
        }

        // Client sends non-standard delimiters here, parsing..
        if (contents.contains("/")) {
            String roomIdStr = contents.split("/")[0];

            if (StringUtils.isNumeric(roomIdStr)) {
                roomId = Integer.parseInt(roomIdStr);
            }

            password = contents.split("/")[1];
        } else {
            roomId = Integer.parseInt(contents);
        }

        Room room = RoomManager.getInstance().getRoomById(roomId);

        if (room == null) {
            room = RoomDao.getRoomById(roomId);
        }

        if (room == null) {
            return;
        }

        if (!player.hasFuse(Fuseright.ENTER_LOCKED_ROOMS) && player.getRoomUser().getAuthenticateId() != roomId) {
            if (room.getData().getAccessTypeId() == 1 && !room.hasRights(player.getDetails().getId(), false) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {

                if (rangDoorbell(room, player)) {
                    player.send(new DOORBELL_WAIT());
                } else {
                    player.send(new FLATNOTALLOWEDTOENTER());
                }

                return;
            }

            if (room.getData().getAccessTypeId() == 2 && !room.isOwner(player.getDetails().getId()) && !player.hasFuse(Fuseright.ANY_ROOM_CONTROLLER)) {
                if (!password.equals(room.getData().getPassword())) {
                    player.send(new LOCALISED_ERROR("Incorrect flat password"));
                    return;
                }
            }
        }

        player.getRoomUser().setAuthenticateId(roomId);
        player.send(new FLAT_LETIN());
    }

    private boolean rangDoorbell(Room room, Player player) {
        boolean sentWithRights = false;

        for (Player user : room.getEntityManager().getPlayers()) {
            if (!room.hasRights(user.getDetails().getId())) {
                continue;
            }

            user.send(new DOORBELL_WAIT(player.getDetails().getName()));
            sentWithRights = true;
        }

        return sentWithRights;
    }
}
