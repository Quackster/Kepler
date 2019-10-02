package org.alexdev.kepler.messages.outgoing.recycler;

import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.recycler.RecyclerReward;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class RECYCLER_CONFIGURATION extends MessageComposer {
    private final boolean isRecyclerEnabled;
    private final List<RecyclerReward> recyclerRewards;

    public RECYCLER_CONFIGURATION(boolean isRecyclerEnabled, List<RecyclerReward> recyclerRewards) {
        this.isRecyclerEnabled = isRecyclerEnabled;
        this.recyclerRewards = recyclerRewards;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeBool(this.isRecyclerEnabled);//tServiceEnabled = tConn.GetIntFrom()
        response.writeInt(180);//tQuarantineMinutes = tConn.GetIntFrom()
        response.writeInt(120);//tRecyclingMinutes = tConn.GetIntFrom()
        response.writeInt(60);//tMinutesToTimeout = tConn.GetIntFrom()
        response.writeInt(this.recyclerRewards.size());//tNumOfRewardItems = tConn.GetIntFrom()

        for (RecyclerReward recyclerReward : this.recyclerRewards) {
            response.writeInt(recyclerReward.getItemCost());

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

    @Override
    public short getHeader() {
        return 303; // "Do"
    }
}
