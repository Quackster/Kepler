package net.h4bbo.kepler.messages.incoming.club;

import net.h4bbo.kepler.game.club.ClubSubscription;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.log.Log;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

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
