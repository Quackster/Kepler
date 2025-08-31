package org.alexdev.kepler.messages.incoming.club;

import org.alexdev.kepler.game.club.ClubSubscription;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.club.CLUB_GIFT;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

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
