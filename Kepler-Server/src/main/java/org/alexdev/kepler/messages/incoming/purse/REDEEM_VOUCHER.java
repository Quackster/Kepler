package org.alexdev.kepler.messages.incoming.purse;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.messages.outgoing.purse.VOUCHER_REDEEM_ERROR;
import org.alexdev.kepler.messages.outgoing.purse.VOUCHER_REDEEM_OK;
import org.alexdev.kepler.messages.outgoing.purse.VOUCHER_REDEEM_ERROR.RedeemError;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class REDEEM_VOUCHER implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        String voucher = reader.readString();
        //player.send(new VOUCHER_REDEEM_OK());
        player.send(new VOUCHER_REDEEM_ERROR(RedeemError.PRODUCT_DELIVERY_FAILED));
    }
}
