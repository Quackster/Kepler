package net.h4bbo.kepler.messages.incoming.purse;

import net.h4bbo.kepler.game.catalogue.CatalogueItem;
import net.h4bbo.kepler.game.catalogue.voucher.VoucherManager;
import net.h4bbo.kepler.game.catalogue.voucher.VoucherRedeemMode;
import net.h4bbo.kepler.game.catalogue.voucher.VoucherRedeemStatus;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;
import net.h4bbo.kepler.messages.outgoing.purse.VOUCHER_REDEEM_ERROR;
import net.h4bbo.kepler.messages.outgoing.purse.VOUCHER_REDEEM_OK;
import net.h4bbo.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class REDEEM_VOUCHER implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) throws SQLException {
        if (!player.isLoggedIn()) {
            return;
        }

        AtomicInteger redeemedCredits = new AtomicInteger(0);
        var redeemedItem = new ArrayList<CatalogueItem>();

        var voucherStatus = VoucherManager.getInstance().redeem(player.getDetails(), VoucherRedeemMode.IN_GAME, reader.readString(), redeemedItem, redeemedCredits);

        if (voucherStatus == VoucherRedeemStatus.FAILURE) {
            player.send(new VOUCHER_REDEEM_ERROR(VOUCHER_REDEEM_ERROR.RedeemError.INVALID));
            return;
        }

        if (voucherStatus == VoucherRedeemStatus.FAILURE_NEW_ACCOUNT) {
            player.send(new ALERT("Sorry, your account is too new and cannot redeem this voucher"));
            return;
        }

        player.send(new VOUCHER_REDEEM_OK(redeemedItem));

        if (redeemedCredits.get() > 0) {
            player.send(new CREDIT_BALANCE(player.getDetails().getCredits()));
        }

        if (redeemedItem.size() > 0) {
            player.getInventory().reload();

            if (player.getRoomUser().getRoom() != null)
                player.getInventory().getView("new");
        }
    }
}
