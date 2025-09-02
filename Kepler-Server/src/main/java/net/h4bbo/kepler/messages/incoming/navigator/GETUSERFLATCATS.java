package net.h4bbo.kepler.messages.incoming.navigator;

import net.h4bbo.kepler.game.navigator.NavigatorCategory;
import net.h4bbo.kepler.game.navigator.NavigatorManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.messages.outgoing.navigator.USERFLATCATS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;

public class GETUSERFLATCATS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        List<NavigatorCategory> categoryList = new ArrayList<>();

        for (NavigatorCategory category : NavigatorManager.getInstance().getCategories().values()) {
            if (category.isPublicSpaces()) {
                continue;
            }

            if (category.getMinimumRoleAccess().getRankId() > player.getDetails().getRank().getRankId()) {
                continue;
            }

            categoryList.add(category);
        }

        player.send(new USERFLATCATS(categoryList));
    }
}
