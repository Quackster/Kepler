package net.h4bbo.kepler.messages.incoming.recycler;

import net.h4bbo.kepler.dao.mysql.RecyclerDao;
import net.h4bbo.kepler.game.catalogue.CatalogueManager;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.recycler.RecyclerSession;
import net.h4bbo.kepler.messages.outgoing.recycler.START_RECYCLING_RESULT;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;
import net.h4bbo.kepler.util.DateUtil;

import java.util.List;

public class CONFIRM_FURNI_RECYCLING implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        RecyclerSession recyclerSession = RecyclerDao.getSession(player.getDetails().getId());

        if (recyclerSession == null) {
            return;
        }

        boolean isCancel = !reader.readBoolean();

        if (!isCancel) {
            if (!recyclerSession.isRecyclingDone() || recyclerSession.hasTimeout()) {
                return;
            }
        }

        for (int itemId : recyclerSession.getItems()) {
            Item item = player.getInventory().getItem(itemId);

            if (item == null) {
                continue;
            }

            if (isCancel) {
                item.setHidden(false);
                item.save();

                player.getInventory().addItem(item);
            } else {
                player.getInventory().getItems().remove(item);
                item.delete();
            }
        }

        RecyclerDao.deleteSession(player.getDetails().getId());
        if (!isCancel) {
            List<Item> itemList = CatalogueManager.getInstance().purchase(player.getDetails(), recyclerSession.getRecyclerReward().getCatalogueItem(), null, null, DateUtil.getCurrentTimeSeconds());

            if (!itemList.isEmpty()) {
                player.getInventory().getView("new");
            }

        }
        player.send(new START_RECYCLING_RESULT(true));
        player.getInventory().getView("new");
    }
}
