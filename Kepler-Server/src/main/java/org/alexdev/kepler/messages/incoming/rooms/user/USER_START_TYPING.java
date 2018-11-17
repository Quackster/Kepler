package org.alexdev.kepler.messages.incoming.rooms.user;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.user.TYPING_STATUS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class USER_START_TYPING implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null || player.getRoomUser().isTyping()) {
            return;
        }

        if (player.getRoomUser().getGamePlayer() != null &&
            player.getRoomUser().getGamePlayer().getGame() != null &&
            player.getRoomUser().getGamePlayer().isInGame()) {
            return;
        }

        player.getRoomUser().getTimerManager().beginChatBubbleTimer();
        player.getRoomUser().setTyping(true);

        room.send(new TYPING_STATUS(player.getRoomUser().getInstanceId(), player.getRoomUser().isTyping()));
    }
}
