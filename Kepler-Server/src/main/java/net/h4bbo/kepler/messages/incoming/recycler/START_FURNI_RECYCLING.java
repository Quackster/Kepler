package net.h4bbo.kepler.messages.incoming.recycler;

import net.h4bbo.kepler.dao.mysql.RecyclerDao;
import net.h4bbo.kepler.game.item.Item;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.recycler.RecyclerManager;
import net.h4bbo.kepler.game.recycler.RecyclerReward;
import net.h4bbo.kepler.messages.outgoing.recycler.RECYCLER_STATUS;
import net.h4bbo.kepler.messages.types.MessageEvent;
import net.h4bbo.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class START_FURNI_RECYCLING implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        List<Item> items = new ArrayList<>();
        boolean canRecycle = false;

        for (int i = 0; i < 2; i++) {
            int count = reader.readInt();

            for (int j = 0; j < count; j++) {
                int itemId = reader.readInt();
                Item item = player.getInventory().getItem(itemId);

                if (item == null || !item.getDefinition().isRecyclable()) {
                    continue;
                }

                items.add(item);
            }
        }

        RecyclerReward recyclerReward = RecyclerManager.getInstance().getRecyclerRewards().stream().filter(reward -> reward.getItemCost() == items.size()).findFirst().orElse(null);

        if (recyclerReward != null) {
            if (RecyclerDao.getSession(player.getDetails().getId()) == null) {
                canRecycle = true;
            }
        }

        if (canRecycle) {
            for (Item item : items) {
                item.setHidden(true);
                item.save();
            }

            player.getInventory().getView("new");
            var session = RecyclerDao.createSession(player.getDetails().getId(), recyclerReward.getId(), items.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.joining(",")));

            player.send(new RECYCLER_STATUS(
                    RecyclerManager.getInstance().isRecyclerEnabled(),
                    session));
        }
    }
}
