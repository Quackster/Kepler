package org.alexdev.kepler.messages.outgoing.user;

import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

public class CLUB_INFO extends MessageComposer {
    private final int remainingDaysThisMonth;
    private final int sinceMonths;
    private final int prepaidMonths;

    public CLUB_INFO(int remaining_days_for_this_month, int since_months, int prepaid_months) {
        this.remainingDaysThisMonth = remaining_days_for_this_month;
        this.sinceMonths = since_months;
        this.prepaidMonths = prepaid_months;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeString("club_habbo");
        response.writeInt(this.remainingDaysThisMonth);
        response.writeInt(this.sinceMonths);
        response.writeInt(this.prepaidMonths);
        response.writeInt(1); // When set to 2, the Habbo club dialogue opens.
    }

    @Override
    public short getHeader() {
        return 7; // "@G"
    }
}
