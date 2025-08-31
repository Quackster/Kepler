package org.alexdev.kepler.messages.outgoing.rooms.badges;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class USER_BADGE extends MessageComposer {
    private int instanceId;
    private String currentBadge;
    private boolean showBadge;

    public USER_BADGE(int instanceId, Player player) {
        this.instanceId = instanceId;

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
        response.writeInt(this.instanceId);

        if (this.showBadge) {
            response.writeString(this.currentBadge);
        }
    }

    @Override
    public short getHeader() {
        return 228; // "Cd"
    }
}
