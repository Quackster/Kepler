package org.alexdev.havana.messages.incoming.rooms.user;

import org.alexdev.havana.game.player.Player;
import org.alexdev.havana.game.player.PlayerManager;
import org.alexdev.havana.game.room.Room;
import org.alexdev.havana.game.wordfilter.WordfilterManager;
import org.alexdev.havana.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.havana.messages.outgoing.rooms.user.CHAT_MESSAGE.ChatMessageType;
import org.alexdev.havana.messages.types.MessageEvent;
import org.alexdev.havana.server.netty.streams.NettyRequest;
import org.alexdev.havana.util.StringUtil;
import org.alexdev.havana.util.config.GameConfiguration;

public class WHISPER implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        Room room = player.getRoomUser().getRoom();

        if (room == null) {
            return;
        }

        String contents = reader.readString();

        String username = contents.substring(0, contents.indexOf(" "));
        String message = StringUtil.filterInput(contents.substring(username.length() + 1), true);

        if (message.isEmpty()) {
            return;
        }

        if (GameConfiguration.getInstance().getBoolean("wordfitler.enabled")) {
            for (String word : WordfilterManager.getInstance().getBannedWords()) {
                if (message.contains(word)) {
                    message = message.replace(word, GameConfiguration.getInstance().getString("wordfilter.word.replacement"));
                }
            }
        }

        CHAT_MESSAGE chatMessage = new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), message, 0);

        player.send(chatMessage);
        player.getRoomUser().getTimerManager().resetRoomTimer();

        Player whisperUser = PlayerManager.getInstance().getPlayerByName(username);

        if (whisperUser != null) {
            if (!whisperUser.getIgnoredList().contains(player.getDetails().getName())) {
                whisperUser.send(chatMessage);
            }
        }/* else {
            player.send(new CHAT_MESSAGE(ChatMessageType.WHISPER, player.getRoomUser().getInstanceId(), "User not found. Whisper not sent.", 0));
        }*/
    }
}
