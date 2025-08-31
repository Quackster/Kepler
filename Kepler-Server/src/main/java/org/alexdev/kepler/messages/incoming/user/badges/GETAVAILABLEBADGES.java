package org.alexdev.kepler.messages.incoming.user.badges;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.guides.INIT_TUTOR_SERVICE_STATUS;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class GETAVAILABLEBADGES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (!player.isLoggedIn()) {
            return;
        }

        player.getBadgeManager().refreshBadges();
        player.getAchievementManager().processAchievements(player, true);

        if (player.getGuideManager().isGuide()) {
            player.send(new INIT_TUTOR_SERVICE_STATUS(1));
        }
    }
}
