package org.alexdev.kepler.messages.incoming.rooms.badges;

import org.alexdev.kepler.game.badges.Badge;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.badges.USER_BADGE;
import org.alexdev.kepler.messages.outgoing.user.badges.USERBADGE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SETBADGE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        reader.readInt();

        String newBadge = reader.readString();

        if (!player.getBadgeManager().hasBadge(newBadge)) {
            return;
        }

        boolean showBadge = reader.readBoolean();

        // Unequip all previous badges
        for (Badge badge : player.getBadgeManager().getBadges()) {
            player.getBadgeManager().changeBadge(badge.getBadgeCode(), false, 0);
        }

        player.getBadgeManager().changeBadge(newBadge, showBadge, 1);

        if (player.getRoomUser().getRoom() != null) {
            player.getRoomUser().getRoom().send(new USER_BADGE(player.getRoomUser().getInstanceId(), player));
        }

        player.getBadgeManager().refreshBadges();
        player.getBadgeManager().saveQueuedBadges();
    }
}
