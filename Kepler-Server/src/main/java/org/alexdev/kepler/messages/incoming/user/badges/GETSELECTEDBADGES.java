package org.alexdev.kepler.messages.incoming.user.badges;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.user.badges.USERBADGE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

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
