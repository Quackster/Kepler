package org.alexdev.kepler.messages.incoming.rooms.items;

import org.alexdev.kepler.dao.mysql.CurrencyDao;
import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.game.room.tasks.CameraTask;
import org.alexdev.kepler.messages.outgoing.rooms.user.USER_STATUSES;
import org.alexdev.kepler.messages.outgoing.user.currencies.FILM;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class USEITEM implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (!player.getRoomUser().containsStatus(StatusType.CARRY_ITEM)) {
            return;
        }
        
        String item = player.getRoomUser().getStatus(StatusType.CARRY_ITEM).getValue();

        player.getRoomUser().getStatuses().remove(StatusType.CARRY_ITEM.getStatusCode());
        player.getRoomUser().setStatus(StatusType.USE_ITEM, item);

        if (!player.getRoomUser().isWalking()) {
            player.getRoomUser().getRoom().send(new USER_STATUSES(List.of(player)));
        }

        GameScheduler.getInstance().getSchedulerService().schedule(new CameraTask(player), 1, TimeUnit.SECONDS);
    }
}
