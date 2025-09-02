package net.h4bbo.kepler.messages.incoming.user.badges;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.guides.INIT_TUTOR_SERVICE_STATUS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
