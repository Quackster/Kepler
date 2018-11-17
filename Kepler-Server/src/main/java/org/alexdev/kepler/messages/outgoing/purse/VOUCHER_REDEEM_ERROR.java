package org.alexdev.kepler.messages.outgoing.purse;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class VOUCHER_REDEEM_ERROR extends MessageComposer {
    public enum RedeemError {
        TECHNICAL_ERROR(0),
        INVALID(1),
        PRODUCT_DELIVERY_FAILED(2),
        WEB_ONLY(3);

        private int errorCode;

        RedeemError(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return this.errorCode;
        }
    }

    private final RedeemError error;

    public VOUCHER_REDEEM_ERROR(RedeemError error) {
        this.error = error;
    }

    @Override
    public void compose(NettyResponse response) {
        // if ERROR = 213 then
        //   me.getInterface().setVoucherInput(1)
        //   tDelim = the itemDelimiter
        //   the itemDelimiter = "\t"
        //   -- UNK_B0 257
        //   tErrorCode = 1.getPropRef().getProp(#item, 1)
        //   the itemDelimiter = tDelim
        //   -- UNK_65 1
        //   return(executeMessage(#alert, [#Msg:"purse_vouchers_error" & tErrorCode]))
        // end if
        response.write(this.error.getErrorCode());
    }

    @Override
    public short getHeader() {
        return 213; // "CU"
    }
}