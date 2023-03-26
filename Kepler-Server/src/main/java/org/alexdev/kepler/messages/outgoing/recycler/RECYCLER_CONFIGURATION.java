package org.alexdev.kepler.messages.outgoing.recycler;

import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.recycler.RecyclerReward;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RECYCLER_CONFIGURATION extends MessageComposer {
    private final boolean isRecyclerEnabled;
    private final List<RecyclerReward> recyclerRewards;
    private final int recyclerTimeoutSeconds;
    private final int recyclerItemQuarantineSeconds;
    private final int recyclerSessionLengthSeconds;

    public RECYCLER_CONFIGURATION(boolean isRecyclerEnabled, List<RecyclerReward> recyclerRewards, int recyclerTimeoutSeconds, int recyclerItemQuarantineSeconds, int recyclerSessionLengthSeconds) {
        this.isRecyclerEnabled = isRecyclerEnabled;
        this.recyclerRewards = recyclerRewards;
        this.recyclerTimeoutSeconds = recyclerTimeoutSeconds;
        this.recyclerItemQuarantineSeconds = recyclerItemQuarantineSeconds;
        this.recyclerSessionLengthSeconds = recyclerSessionLengthSeconds;

    }

    @Override
    public void compose(NettyResponse response) {
        response.writeBool(this.isRecyclerEnabled);//tServiceEnabled = tConn.GetIntFrom()
        response.writeInt((int) TimeUnit.SECONDS.toMinutes(this.recyclerItemQuarantineSeconds));//tQuarantineMinutes = tConn.GetIntFrom()
        response.writeInt((int) TimeUnit.SECONDS.toMinutes(this.recyclerSessionLengthSeconds));//tRecyclingMinutes = tConn.GetIntFrom()
        response.writeInt((int) TimeUnit.SECONDS.toMinutes(this.recyclerTimeoutSeconds));//tMinutesToTimeout = tConn.GetIntFrom()
        response.writeInt(this.recyclerRewards.size());//tNumOfRewardItems = tConn.GetIntFrom()

        for (RecyclerReward recyclerReward : this.recyclerRewards) {
            response.writeInt(recyclerReward.getItemCost());
            if(recyclerReward.getSaleCode().equalsIgnoreCase("tickets")) {
                response.writeInt(2);
                response.writeString("20 Billetter");
            } else {
                var definition = recyclerReward.getCatalogueItem().getDefinition();

                response.writeBool(definition.hasBehaviour(ItemBehaviour.WALL_ITEM));
                response.writeString(recyclerReward.getCatalogueItem().getDefinition().getSprite());

                if (definition.hasBehaviour(ItemBehaviour.WALL_ITEM)) {
                    if (recyclerReward.getCatalogueItem().getItemSpecialId() > 0) {
                        response.writeString(" ");
                        response.writeString(recyclerReward.getCatalogueItem().getItemSpecialId());
                    }
                } else {
                    response.writeInt(0); // Default direction
                    response.writeInt(definition.getLength());
                    response.writeInt(definition.getWidth());
                    response.writeString(definition.getColour());
                }
            }
        }
    }

    @Override
    public short getHeader() {
        return 303; // "Do"
    }
}
