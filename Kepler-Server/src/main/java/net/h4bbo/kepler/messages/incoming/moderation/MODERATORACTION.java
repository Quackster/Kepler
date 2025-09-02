package net.h4bbo.kepler.messages.incoming.moderation;

import net.h4bbo.kepler.game.moderation.ModerationActionType;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class MODERATORACTION implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        int targetType = reader.readInt();
        int actionType = reader.readInt();

        String alertMessage = reader.readString();
        String notes = reader.readString();

        for (ModerationActionType moderationActionType : ModerationActionType.values()) {
            if (moderationActionType.getTargetType() == targetType &&
                moderationActionType.getActionType() == actionType) {
                moderationActionType.getModerationAction().performAction(player, player.getRoomUser().getRoom(), alertMessage, notes, reader);
            }
        }
    }
}
