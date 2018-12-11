package org.alexdev.kepler.messages.incoming.rooms.pool;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.apache.commons.lang3.StringUtils;

public class SIGN implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        String contents = reader.contents();

        if (!StringUtils.isNumeric(contents)) {
            return;
        }

        int vote = Integer.parseInt(contents);

        if (vote < 0) {
            return;
        }

        if (vote <= 7) {
            player.getRoomUser().setLidoVote(vote + 3);
        }

        player.getRoomUser().setStatus(StatusType.SIGN, contents, 5, null, -1, -1);
        player.getRoomUser().setNeedsUpdate(true);

        player.getRoomUser().getTimerManager().resetRoomTimer();
    }
}