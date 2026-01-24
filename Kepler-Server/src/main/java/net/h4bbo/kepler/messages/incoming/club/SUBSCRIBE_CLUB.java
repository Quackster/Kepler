package net.h4bbo.kepler.messages.incoming.club;

import net.h4bbo.kepler.game.club.ClubSubscription;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.club.CLUB_GIFT;
import net.h4bbo.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

public class SUBSCRIBE_CLUB implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        reader.readString();

        int choice = reader.readInt();

        if (ClubSubscription.subscribeClub(player.getDetails(), choice)) {
            player.send(new CREDIT_BALANCE(player.getDetails().getCredits()));
            player.refreshClub();
        }
    }
}
