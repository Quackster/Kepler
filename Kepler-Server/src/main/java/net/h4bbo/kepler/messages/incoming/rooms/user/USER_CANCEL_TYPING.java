package net.h4bbo.kepler.messages.incoming.rooms.user;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.Room;
import net.h4bbo.kepler.messages.outgoing.rooms.user.TYPING_STATUS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class USER_CANCEL_TYPING implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null || !player.getRoomUser().isTyping()) {
            return;
        }

        if (player.getRoomUser().getGamePlayer() != null &&
            player.getRoomUser().getGamePlayer().getGame() != null &&
            player.getRoomUser().getGamePlayer().isInGame()) {
            return;
        }

        player.getRoomUser().getTimerManager().stopChatBubbleTimer();
        player.getRoomUser().setTyping(false);

        room.send(new TYPING_STATUS(player.getRoomUser().getInstanceId(), player.getRoomUser().isTyping()));
    }
}
