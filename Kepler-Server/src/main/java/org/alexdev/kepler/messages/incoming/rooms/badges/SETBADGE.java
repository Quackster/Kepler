package org.alexdev.kepler.messages.incoming.rooms.badges;

import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.rooms.badges.USER_BADGE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SETBADGE implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        reader.readInt();

        String newBadge = reader.readString();

        if (!player.getDetails().getBadges().contains(newBadge)) {
            return;
        }

        boolean showBadge = reader.readBoolean();

        player.getDetails().setCurrentBadge(newBadge);
        player.getDetails().setShowBadge(showBadge);

        if (player.getRoomUser().getRoom() != null) {
            player.getRoomUser().getRoom().send(new USER_BADGE(player.getRoomUser().getInstanceId(), player.getDetails()));
        }

        PlayerDao.saveCurrentBadge(player.getDetails());
    }
}