package org.alexdev.kepler.messages.incoming.club;

import org.alexdev.kepler.game.club.ClubSubscription;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.sql.SQLException;

public class SCR_GIFT_APPROVAL implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        if (ClubSubscription.isGiftDue(player)) {

            try {
                ClubSubscription.tryNextGift(player);
            } catch (SQLException e) {
                Log.getErrorLogger().error("Error trying to process club gift for user (" + player.getDetails().getName() + "): ", e);
            }
        }
    }
}
