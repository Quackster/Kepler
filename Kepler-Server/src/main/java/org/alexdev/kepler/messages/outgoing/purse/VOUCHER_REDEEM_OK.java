package org.alexdev.kepler.messages.outgoing.purse;

import org.alexdev.kepler.game.catalogue.CatalogueItem;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;
import java.util.Map;

import static org.apache.batik.svggen.font.table.GlyfDescript.repeat;

public class VOUCHER_REDEEM_OK extends MessageComposer {
    private final List<CatalogueItem> redeemableItems;

    public VOUCHER_REDEEM_OK(List<CatalogueItem> redeemableItems) {
        this.redeemableItems = redeemableItems;
    }

    @Override
    public void compose(NettyResponse response) {
//        if tMsg.subject = 212 then
//        me.getInterface().hideVoucherWindow()
//        me.getInterface().setVoucherInput(1)
//        tConn = tMsg.connection
//        if tConn = void() then
//        return(1)
//        end if
//        tProductName = tConn.GetStrFrom()
//        if tProductName <> "" then
//                tResultStr = getText("purse_vouchers_furni_success") & "\r" & "\r"
//        repeat while tProductName <> ""
//        tDescription = tConn.GetStrFrom()
//        tResultStr = tResultStr & tProductName & "\r"
//        tProductName = tConn.GetStrFrom()
//        end repeat
//        return(executeMessage(#alert, [#Msg:tResultStr]))
//        else
//        return(executeMessage(#alert, [#Msg:"purse_vouchers_success"]))
//        end if
//      else

        if (this.redeemableItems != null) {
            for (CatalogueItem item : this.redeemableItems) {
                response.writeString(item.getName());
                response.writeString(item.getDescription());
            }
        }
    }

    @Override
    public short getHeader() {
        return 212; // "CT"
    }
}