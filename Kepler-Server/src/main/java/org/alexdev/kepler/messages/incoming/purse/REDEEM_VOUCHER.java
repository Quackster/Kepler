package org.alexdev.kepler.messages.incoming.purse;

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

        if(voucher == null)
        {
            //No voucher was found
            player.send(new VOUCHER_REDEEM_ERROR(RedeemError.INVALID));
        }
        else
        {
            //Redeem items
            List<Item> redeemedItems = new ArrayList<>();
            List<ItemDefinition> itemDefinitions = voucher.getItemDefinitions();
            
            if (itemDefinitions != null) {
                for (ItemDefinition itemDef : itemDefinitions) {
                    
                    //Setup item custom data
                    String customData = "";
                    
                    if (itemDef.hasBehaviour(ItemBehaviour.POST_IT)) {
                        customData = "20";
                    }

                    if (itemDef.hasBehaviour(ItemBehaviour.ROOMDIMMER)) {
                        customData = Item.DEFAULT_ROOMDIMMER_CUSTOM_DATA;
                    }

                    //Create new item and setup info
                    Item item = new Item();
                    item.setOwnerId(player.getDetails().getId());
                    item.setDefinitionId(itemDef.getId());
                    item.setCustomData(customData);

                    ItemDao.newItem(item);
                    
                    //Add to inventory
                    player.getInventory().addItem(item);
                    
                    redeemedItems.add(item);
                }
            }

            //A voucher was found with more than 0 credits
            player.send(new VOUCHER_REDEEM_OK(redeemedItems));
            CurrencyDao.increaseCredits(player.getDetails(), voucher.getCredits());
            player.send(new CREDIT_BALANCE(player.getDetails()));
        }
    }
}
