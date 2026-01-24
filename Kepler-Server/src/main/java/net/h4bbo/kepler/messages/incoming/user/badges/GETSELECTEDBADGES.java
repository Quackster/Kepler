package net.h4bbo.kepler.messages.incoming.user.badges;

import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.player.PlayerManager;
import net.h4bbo.kepler.messages.outgoing.user.badges.USERBADGE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class GETSELECTEDBADGES implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (reader.contents().isEmpty()) {
            return;
        }

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        //if (player.getRoomUser().getLastBadgeRequest() > DateUtil.getCurrentTimeSeconds()) {
        //    return;
        //}

        int userId = reader.readInt();

        Player badgePlayer = PlayerManager.getInstance().getPlayerById(userId);

        if (badgePlayer == null) {
            return;
        }

        player.send(new USERBADGE(userId, badgePlayer.getBadgeManager().getEquippedBadges()));
        //player.getRoomUser().setLastBadgeRequest(DateUtil.getCurrentTimeSeconds() + 5);
    }
}
