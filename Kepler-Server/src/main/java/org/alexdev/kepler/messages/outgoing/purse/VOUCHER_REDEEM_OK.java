package org.alexdev.kepler.messages.outgoing.purse;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;
import java.util.Map;

public class VOUCHER_REDEEM_OK extends MessageComposer {
    private final List<Item> redeemableItems;

    public VOUCHER_REDEEM_OK() {
        this.redeemableItems = null;
    }

    public VOUCHER_REDEEM_OK(List<Item> redeemableItems) {
        this.redeemableItems = redeemableItems;
    }

    @Override
    public void compose(NettyResponse response) {
//        tProductName = tConn.GetStrFrom()
//        if tProductName <> "" then
//                tResultStr = getText("purse_vouchers_furni_success") & "\r" & "\r"
//        repeat while tProductName <> ""
//        tDescription = tConn.GetStrFrom()
//        tResultStr = tResultStr & tProductName & "\r"
//        tProductName = tConn.GetStrFrom()
//        end repeat
//        -- UNK_65 1
//        return(executeMessage(#alert, [#Msg:tResultStr]))
//        else
//        -- UNK_65 1
//        return(executeMessage(#alert, [#Msg:"purse_vouchers_success"]))
//        end if

        if (this.redeemableItems != null) {
            for (Item item : this.redeemableItems) {
                response.writeString(item.getDefinition().getName(0));
                response.writeString(item.getDefinition().getDescription(0));
            }
        }
    }

    @Override
    public short getHeader() {
        return 212; // "CT"
    }
}