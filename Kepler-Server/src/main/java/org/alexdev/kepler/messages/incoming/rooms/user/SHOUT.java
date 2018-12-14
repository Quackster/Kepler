package org.alexdev.havana.messages.incoming.rooms.user;

import org.alexdev.havana.game.commands.CommandManager;
import org.alexdev.havana.game.player.Player;
import org.alexdev.havana.game.room.Room;
import org.alexdev.havana.messages.outgoing.rooms.user.TYPING_STATUS;
import org.alexdev.havana.messages.types.MessageEvent;
import org.alexdev.havana.server.netty.streams.NettyRequest;
import org.alexdev.havana.util.StringUtil;

public class SHOUT implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        if (player.getRoomUser().getGamePlayer() != null &&
                player.getRoomUser().getGamePlayer().getGame() != null &&
                player.getRoomUser().getGamePlayer().getGame().isArenaLoaded()) {
            //room.send(new GAME_CHAT(player.getRoomUser().getGamePlayer().getObjectId(), message));
            return;
        }

        String message = StringUtil.filterInput(reader.readString(), true);

        player.getRoomUser().setTyping(false);
        room.send(new TYPING_STATUS(player.getRoomUser().getInstanceId(), player.getRoomUser().isTyping()));

        if (message.isEmpty()) {
            return;
        }

        if (CommandManager.getInstance().hasCommand(player, message)) {
            CommandManager.getInstance().invokeCommand(player, message);
            return;
        }

        player.getRoomUser().chat(message, true);
        player.getRoomUser().getTimerManager().resetRoomTimer();
    }
}
