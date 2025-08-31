package org.alexdev.kepler.messages.outgoing.user.badges;

import org.alexdev.kepler.game.badges.Badge;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class USERBADGE extends MessageComposer {
    private final int userId;
    private final List<Badge> equippedBadges;

    public USERBADGE(int userId, List<Badge> equippedBadges) {
        this.userId = userId;
        this.equippedBadges = equippedBadges;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString(this.userId);
        response.writeInt(this.equippedBadges.size());

        for (Badge badge : this.equippedBadges) {
            response.writeInt(badge.getSlotId());
            response.writeString(badge.getBadgeCode());
        }
    }

    @Override
    public short getHeader() {
        return 228; // "Cd"
    }
}
