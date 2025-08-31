package org.alexdev.kepler.messages.incoming.rooms.pool;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.public_rooms.PoolHandler;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_OBJECTS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.util.StringUtil;

public class SWIMSUIT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (!room.getData().getModel().equals("pool_a") &&
                !room.getData().getModel().equals("md_a")) {
            return;
        }

        String swimsuit = StringUtil.filterInput(reader.contents(), true);
        player.getDetails().setPoolFigure(swimsuit);

        room.send(new USER_OBJECTS(player));

        PlayerDao.saveDetails(
                player.getDetails().getId(),
                player.getDetails().getFigure(),
                player.getDetails().getPoolFigure(),
                player.getDetails().getSex());
    }
}
