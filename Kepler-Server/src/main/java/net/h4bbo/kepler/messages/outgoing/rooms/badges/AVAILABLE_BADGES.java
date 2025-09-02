package net.h4bbo.kepler.messages.outgoing.rooms.badges;

import net.h4bbo.kepler.game.badges.Badge;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class AVAILABLE_BADGES extends MessageComposer {
    private final List<Badge> badges;
    private final String currentBadge;
    private final boolean showBadge;

    public AVAILABLE_BADGES(Player player) {
        this.badges = player.getBadgeManager().getBadges();
        var badge = player.getBadgeManager().getEquippedBadges().stream().findFirst();

        if (badge.isPresent()) {
            this.currentBadge = badge.get().getBadgeCode(); //playerDetails.getCurrentBadge();
            this.showBadge = badge.get().isEquipped();//playerDetails.getShowBadge();
        } else {
            this.currentBadge = null;
            this.showBadge = false;
        }
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.badges.size());

        var badgeSlot = 0;
        var slotCounter = 0;

        for (Badge badge : this.badges) {
            response.writeString(badge.getBadgeCode());

            if (badge.getBadgeCode().equals(this.currentBadge)) {
                badgeSlot = slotCounter;
            }

            slotCounter++;
        }

        response.writeInt(badgeSlot);
        response.writeBool(this.showBadge);
    }

    @Override
    public short getHeader() {
        return 229; // "Ce"
    }
}