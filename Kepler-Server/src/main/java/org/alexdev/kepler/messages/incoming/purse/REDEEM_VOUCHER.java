package org.alexdev.kepler.messages.incoming.purse;

import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.util.StringUtil;
import org.alexdev.kepler.util.DateUtil;
import org.alexdev.kepler.game.item.base.ItemDefinition;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.dao.mysql.*;
import org.alexdev.kepler.messages.outgoing.purse.VOUCHER_REDEEM_ERROR;
import org.alexdev.kepler.messages.outgoing.purse.VOUCHER_REDEEM_OK;
import org.alexdev.kepler.messages.outgoing.purse.VOUCHER_REDEEM_ERROR.RedeemError;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.messages.incoming.catalogue.GRPC;
import org.alexdev.kepler.game.purse.Voucher;
import org.alexdev.kepler.messages.outgoing.user.currencies.CREDIT_BALANCE;
import org.alexdev.kepler.messages.outgoing.rooms.items.ITEM_DELIVERED;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class REDEEM_VOUCHER implements MessageEvent {

    @Override
    public void handle(Player player, NettyRequest reader) throws SQLException {
        String voucherName = reader.readString();

        //Check and get voucher
        Voucher voucher = PurseDao.redeemVoucher(voucherName);

        //No voucher was found
        if (voucher == null) {
            player.send(new VOUCHER_REDEEM_ERROR(RedeemError.INVALID));
            return;
        }

        //Redeem items
        List<Item> redeemedItems = new ArrayList<>();

        for (String saleCode : voucher.getItems()) {
            var catalogueItem = CatalogueManager.getInstance().getCatalogueItem(saleCode);

            if (catalogueItem == null)
                continue;

            redeemedItems.addAll(CatalogueManager.getInstance().purchase(player, catalogueItem, "", null, DateUtil.getCurrentTimeSeconds()));
        }

        //A voucher was found, so redeem items and redeem credits
        player.send(new VOUCHER_REDEEM_OK(redeemedItems));

        if (redeemedItems.size() > 0) {
            player.getInventory().getView("new");
        }

        //This voucher gives credits, so increase credits balance
        if (voucher.getCredits() > 0) {
            CurrencyDao.increaseCredits(player.getDetails(), voucher.getCredits());
            player.send(new CREDIT_BALANCE(player.getDetails()));
        }
    }
}
