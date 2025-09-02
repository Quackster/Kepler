package net.h4bbo.kepler.messages.incoming.rooms.settings;

import net.h4bbo.kepler.dao.mysql.NavigatorDao;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.models.RoomModelManager;
import net.h4bbo.kepler.game.texts.TextsManager;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;
import net.h4bbo.kepler.messages.outgoing.rooms.settings.GOTO_FLAT;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.StringUtil;

import java.sql.SQLException;

public class CREATEFLAT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws SQLException {
        String[] data = reader.contents().split("/");

        String floorSetting = data[1];
        String roomName = StringUtil.filterInput(data[2], true);
        String roomModel = data[3];
        String roomAccess = data[4];
        boolean roomShowName = Integer.parseInt(data[5]) == 1;

        if (roomName.replace(" ", "").isEmpty()) {
            player.send(new ALERT(TextsManager.getInstance().getValue("roomatic_givename")));
            return;
        }

        if (!floorSetting.equals("first floor")) {
            return;
        }

        if (!roomModel.startsWith("model_")) {
            return;
        }

        if (RoomModelManager.getInstance().getModel(roomModel) == null) {
            return;
        }

        String modelType = roomModel.replace("model_", "");

        if (!modelType.equals("a") &&
                !modelType.equals("b") &&
                !modelType.equals("c") &&
                !modelType.equals("d") &&
                !modelType.equals("e") &&
                !modelType.equals("f") &&
                !modelType.equals("i") &&
                !modelType.equals("j") &&
                !modelType.equals("k") &&
                !modelType.equals("l") &&
                !modelType.equals("m") &&
                !modelType.equals("n") &&
                !player.hasFuse(Fuseright.USE_SPECIAL_ROOM_LAYOUTS)) {
            return; // Fuck off, scripter.
        }

        int accessType = 0;

        if (roomAccess.equals("password")) {
            accessType = 2;
        }

        if (roomAccess.equals("closed")) {
            accessType = 1;
        }

        int roomId = NavigatorDao.createRoom(player.getDetails().getId(), roomName, roomModel, roomShowName, accessType);

        player.getRoomUser().setAuthenticateId(roomId);
        player.send(new GOTO_FLAT(roomId, roomName));
    }
}
