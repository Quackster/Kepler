package org.alexdev.kepler.messages.incoming.fishing;

import org.alexdev.kepler.game.fishing.FishingRoomHandler;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class STARTFISHING implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        final int itemId = reader.readInt();

        player.getLogger().info("STARTFISHING itemId={}", itemId);

        final Room room = player.getRoomUser().getRoom();
        if (room == null) {
            return;
        }

        final FishingRoomHandler fishingRoomHandler = room.getRoomComponentManager().get(FishingRoomHandler.class);
        if (fishingRoomHandler == null) {
            return;
        }

        fishingRoomHandler.onStartFishing(player, itemId);
    }
}
