package net.h4bbo.kepler.messages.incoming.rooms.badges;

import net.h4bbo.kepler.game.badges.Badge;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.rooms.badges.USER_BADGE;
import net.h4bbo.kepler.messages.outgoing.user.badges.USERBADGE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
