package org.alexdev.kepler.messages.outgoing.rooms.badges;

import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class AVAILABLE_BADGES extends MessageComposer {
    private final List<String> badges;
    private final String currentBadge;
    private final boolean showBadge;

    public AVAILABLE_BADGES(PlayerDetails playerDetails) {
        this.badges = playerDetails.getBadges();
        this.currentBadge = playerDetails.getCurrentBadge();
        this.showBadge = playerDetails.getShowBadge();
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.badges.size());

        var badgeSlot = 0;
        var slotCounter = 0;

        for (String badge : this.badges) {
            response.writeString(badge);

            if (badge.equals(this.currentBadge)) {
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
