package net.h4bbo.kepler.messages.outgoing.recycler;

import net.h4bbo.kepler.game.item.base.ItemBehaviour;
import net.h4bbo.kepler.game.recycler.RecyclerSession;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

public class RECYCLER_STATUS extends MessageComposer {
    private final boolean recyclerEnabled;
    private final RecyclerSession session;

    public RECYCLER_STATUS(boolean recyclerEnabled, RecyclerSession session) {
        this.recyclerEnabled = recyclerEnabled;
        this.session = session;
    }

    @Override
    public void compose(NettyResponse response) {
        if (!this.recyclerEnabled || this.session == null) {
            response.writeInt(0);//tStatus = tConn.GetIntFrom()
            return;
        }

        if (this.session.hasTimeout()) {
                response.writeInt(3);
        } else {
            response.writeInt(this.session.isRecyclingDone() ? 2 : 1);
            response.writeInt(this.session.getRecyclerReward().getCatalogueItem().getDefinition().hasBehaviour(ItemBehaviour.WALL_ITEM) ? 1 : 0);
            response.writeString(this.session.getRecyclerReward().getCatalogueItem().getDefinition().getSprite());

            if (!this.session.isRecyclingDone()) {
                response.writeInt(this.session.getMinutesLeft() % 60 == 0 ? this.session.getMinutesLeft() - 1 : this.session.getMinutesLeft());
            }
        }

        /*if (this.session.hasTimeout()) {
            if (this.session.isRecyclingDone()) {
                response.writeInt(2);
                response.writeInt(this.session.getRecyclerReward().getCatalogueItem().getDefinition().hasBehaviour(ItemBehaviour.WALL_ITEM) ? 1 : 0);
                response.writeString(this.session.getRecyclerReward().getCatalogueItem().getDefinition().getSprite());
                response.writeInt(this.session.getMinutesPassed());
            } else {
                response.writeInt(3);
            }
        } else {

        }
        int minutesPassed = this.session.getMinutesPassed();*/
        //response.writeInt(2);//tStatus = tConn.GetIntFrom()
        //response.writeInt(0);
        //response.writeString("sound_set_9");
    }

    @Override
    public short getHeader() {
        return 304; // "Dp"
    }
}
