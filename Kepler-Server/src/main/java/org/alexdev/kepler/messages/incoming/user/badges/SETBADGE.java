package org.alexdev.kepler.messages.incoming.user.badges;


import org.alexdev.kepler.game.badges.Badge;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.user.badges.USERBADGE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SETBADGE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        // Unequip all previous badges
        for (Badge badge : player.getBadgeManager().getBadges()) {
            player.getBadgeManager().changeBadge(badge.getBadgeCode(), false, 0);
        }

        // Equip new badges
        while (reader.contents().length() > 0) {
            int slotId = reader.readInt();
            String badgeCode = reader.readString();

            if (slotId > 0 && slotId < 6 && badgeCode.length() > 0) {
                player.getBadgeManager().changeBadge(badgeCode, true, slotId);
            }
        }

        // Notify users of badge updates
        if (player.getRoomUser().getRoom() != null) {
            player.getRoomUser().getRoom().send(new USERBADGE(player.getDetails().getId(), player.getBadgeManager().getEquippedBadges()));
        }
        
        player.getBadgeManager().refreshBadges();
        player.getBadgeManager().saveQueuedBadges();
    }
}
