package net.h4bbo.kepler.messages.incoming.rooms.pool;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
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