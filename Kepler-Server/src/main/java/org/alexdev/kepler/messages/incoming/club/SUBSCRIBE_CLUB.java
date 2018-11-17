package org.alexdev.kepler.messages.incoming.club;

import org.alexdev.kepler.game.club.ClubSubscription;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class SUBSCRIBE_CLUB implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        reader.readString();

        int days = -1;
        int credits = -1;

        int choice = reader.readInt();

        switch (choice) {
            case 1:
            {
                credits = 25;
                days = 31;
                break;
            }
            case 2:
            {
                credits = 60;
                days = 93;
                break;
            }
            case 3:
            {
                credits = 105;
                days = 186;
                break;
            }
        }

        ClubSubscription.subscribeClub(player, days, credits);
    }
}
