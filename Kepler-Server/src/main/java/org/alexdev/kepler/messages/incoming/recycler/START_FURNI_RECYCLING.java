package org.alexdev.kepler.messages.incoming.recycler;

import org.alexdev.kepler.dao.mysql.RecyclerDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.recycler.RecyclerManager;
import org.alexdev.kepler.game.recycler.RecyclerReward;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.ArrayList;
import java.util.List;

public class START_FURNI_RECYCLING implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        List<Item> items = new ArrayList<>();

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

        if (recyclerReward == null) {
            return;
        }

        if (RecyclerDao.getSession(player.getDetails().getId()) != null) {
            return;
        }

        RecyclerDao.createSession(player.getDetails().getId(), recyclerReward.getId());
    }
}
