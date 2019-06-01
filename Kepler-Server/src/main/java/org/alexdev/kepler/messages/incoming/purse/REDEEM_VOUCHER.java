package org.alexdev.kepler.messages.incoming.purse;

import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.dao.mysql.PurseDao;
import org.alexdev.kepler.messages.outgoing.purse.VOUCHER_REDEEM_ERROR;
import org.alexdev.kepler.messages.outgoing.purse.VOUCHER_REDEEM_OK;
import org.alexdev.kepler.messages.outgoing.purse.VOUCHER_REDEEM_ERROR.RedeemError;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import org.alexdev.kepler.dao.mysql.*;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;

public class REDEEM_VOUCHER implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) {
        String voucher = reader.readString();
		
        //Check and get voucher
        int voucherCredits = PurseDao.redeemVoucher(voucher);

        if(voucherCredits == -1)
        {
            //No voucher was found
            player.send(new VOUCHER_REDEEM_ERROR(RedeemError.INVALID));
        }
        else
        {
            //A voucher was found with more than 0 credits
            player.send(new VOUCHER_REDEEM_OK());
            CurrencyDao.increaseCredits(player.getDetails(), voucherCredits);
            player.send(new CREDIT_BALANCE(player.getDetails()));
        }
    }
}
