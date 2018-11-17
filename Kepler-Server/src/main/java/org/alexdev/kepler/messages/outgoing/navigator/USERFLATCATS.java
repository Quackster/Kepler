package org.alexdev.kepler.messages.outgoing.navigator;

import org.alexdev.kepler.game.navigator.NavigatorCategory;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

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
