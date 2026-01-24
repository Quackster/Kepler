package net.h4bbo.kepler.messages.outgoing.navigator;

import net.h4bbo.kepler.game.navigator.NavigatorCategory;
import net.h4bbo.kepler.messages.types.MessageComposer;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.List;

public class USERFLATCATS extends MessageComposer {
    private final List<NavigatorCategory> categoryList;

    public USERFLATCATS(List<NavigatorCategory> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public void compose(NettyResponse response) {
        response.writeInt(this.categoryList.size());

        for (NavigatorCategory category : this.categoryList) {
            response.writeInt(category.getId());
            response.writeString(category.getName());
        }
    }

    @Override
    public short getHeader() {
        return 221; // "C]"
    }
}
