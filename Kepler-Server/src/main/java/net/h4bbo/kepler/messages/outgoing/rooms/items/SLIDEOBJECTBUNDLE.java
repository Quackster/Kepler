package net.h4bbo.kepler.messages.outgoing.rooms.items;

import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.item.roller.RollingData;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;
import net.h4bbo.kepler.util.StringUtil;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SLIDEOBJECTBUNDLE extends MessageComposer {
    private Item roller;
    private List<RollingData> rollingItems;
    private RollingData rollingEntity;

    private Position position;
    private int destX;
    private int destY;
    private float destZ;
    private int id;

    public SLIDEOBJECTBUNDLE(Item roller, List<RollingData> rollingItems, RollingData rollingEntity) {
        this.roller = roller;
        this.rollingItems = rollingItems;
        this.rollingEntity = rollingEntity;
    }

    public SLIDEOBJECTBUNDLE(Position position, int destX, int destY, float destZ, int id) {
        this.position = position.copy();
        this.destX = destX;
        this.destY =destY;
        this.destZ = destZ;
        this.id = id;
    }

    @Override
    public void compose(NettyResponse response) {
        if (this.roller == null) {
            response.writeInt(this.destX);
            response.writeInt(this.destY);
            response.writeInt(this.position.getX());
            response.writeInt(this.position.getY());
            response.writeInt(1);
            response.writeInt(this.id);
            response.writeString(StringUtil.format(this.destZ));
            response.writeString(StringUtil.format(this.position.getZ()));
            response.writeInt(ThreadLocalRandom.current().nextInt(0, 10000));
            response.writeInt(0);
        } else {
            response.writeInt(this.roller.getPosition().getX());
            response.writeInt(this.roller.getPosition().getY());
            response.writeInt(this.roller.getPosition().getSquareInFront().getX());
            response.writeInt(this.roller.getPosition().getSquareInFront().getY());
            response.writeInt(this.rollingItems.size());

            for (RollingData item : this.rollingItems) {
                response.writeInt(item.getItem().getId());
                response.writeString(StringUtil.format(item.getFromPosition().getZ()));
                response.writeString(StringUtil.format(item.getNextPosition().getZ()));
            }

            response.writeInt(this.roller.getId());
            response.writeInt(this.rollingEntity != null ? 2 : 0);

            if (this.rollingEntity != null) {
                response.writeInt(this.rollingEntity.getEntity().getRoomUser().getInstanceId());
                response.writeString(StringUtil.format(this.rollingEntity.getFromPosition().getZ()));
                response.writeString(StringUtil.format(this.rollingEntity.getDisplayHeight()));
            }
        }
    }

    @Override
    public short getHeader() {
        return 230; // "Cf"
    }
}
