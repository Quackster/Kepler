package org.alexdev.kepler.messages.incoming.rooms.pool;

import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerDetails;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.messages.outgoing.catalogue.NO_CREDITS;
import org.alexdev.kepler.messages.outgoing.user.ALERT;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import org.alexdev.kepler.messages.outgoing.user.NO_USER_FOUND;
import org.alexdev.kepler.messages.outgoing.user.currencies.TICKET_BALANCE;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class BTCKS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        int mode = reader.readInt();
        String ticketsFor = reader.readString();

        if (ticketsFor == null) {
            return;
        }

        int costCredits;
        int ticketsAmount;

        if (mode == 1) {
            costCredits = 1;
            ticketsAmount = 2;
        } else {
            costCredits = 6;
            ticketsAmount = 20;
        }

        if (costCredits > player.getDetails().getCredits()) {
            player.send(new NO_CREDITS());
            return;
        }

        int userId = PlayerDao.getId(ticketsFor);

        if (userId == -1) {
            player.send(new NO_USER_FOUND(ticketsFor));
            return;
        }

        PlayerDetails details = PlayerManager.getInstance().getPlayerData(userId);
        CurrencyDao.increaseTickets(details, ticketsAmount);

        Player ticketPlayer = PlayerManager.getInstance().getPlayerByName(ticketsFor);

        if (ticketPlayer != null) {
            if (userId != player.getDetails().getId()) {
                ticketPlayer.send(new ALERT(player.getDetails().getName() + " has gifted you tickets!"));
            }

            ticketPlayer.send(new TICKET_BALANCE(details.getTickets()));
        }

        player.getRoomUser().getTimerManager().resetRoomTimer();

        CurrencyDao.decreaseCredits(player.getDetails(), costCredits);
        player.send(new CREDIT_BALANCE(player.getDetails()));
    }
}
