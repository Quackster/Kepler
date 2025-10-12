package org.alexdev.kepler.messages.incoming.rooms;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.interactors.InteractionType;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.messages.outgoing.rooms.ACTIVE_OBJECTS;
import org.alexdev.kepler.messages.outgoing.rooms.OBJECTS_WORLD;
import org.alexdev.kepler.messages.outgoing.rooms.items.PLACE_FLOORITEM;
import org.alexdev.kepler.messages.outgoing.rooms.items.SHOWPROGRAM;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;
import org.alexdev.kepler.server.netty.streams.NettyResponse;

import java.util.concurrent.ThreadLocalRandom;

public class G_OBJS implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) {
        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        Room room = player.getRoomUser().getRoom();

        player.sendQueued(new OBJECTS_WORLD(room.getItemManager().getPublicItems()));

        // Weird behaviour, can't interact upon load, see FlatTrigger as workaround
        player.sendQueued(new ACTIVE_OBJECTS(room.getItemManager().getFloorItems().stream()
                .filter(x -> x.getDefinition().getInteractionType() != InteractionType.PET_WATER_BOWL).toList()));

        player.flush();

        player.getMessenger().sendStatusUpdate();
    }
}
