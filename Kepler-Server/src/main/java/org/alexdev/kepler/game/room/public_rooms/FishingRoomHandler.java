package org.alexdev.kepler.game.room.public_rooms;

import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.components.RoomComponent;
import org.alexdev.kepler.messages.outgoing.rooms.items.PLACE_FLOORITEM;

public class FishingRoomHandler implements RoomComponent, Runnable {

    private final Room room;

    public FishingRoomHandler(Room room) {
        this.room = room;
    }

    @Override
    public void run() {
        // Ticks every second.
    }

    @Override
    public void onPlayerAdded(Player player) {
        final Item fishSign = new Item();

        fishSign.setId(1000);
        fishSign.setPosition(new Position(28, 16, 1, 0, 0));
        fishSign.getDefinition().setSprite("fish_sign");
        fishSign.getDefinition().setLength(1);
        fishSign.getDefinition().setWidth(1);

        player.sendQueued(new PLACE_FLOORITEM(fishSign));

        //for (int i = 0; i < 100; i++) {
        //    final Item fishingArea = new Item();
        //
        //    fishingArea.setId(1002 + i);
        //    fishingArea.setPosition(new Position(
        //            ThreadLocalRandom.current().nextInt(0, 45),
        //            ThreadLocalRandom.current().nextInt(0, 45),
        //            ThreadLocalRandom.current().nextInt(-3, 10)));
        //
        //    fishingArea.getDefinition().setSprite("fish_area");
        //    fishingArea.getDefinition().setLength(1);
        //    fishingArea.getDefinition().setWidth(1);
        //
        //    player.sendQueued(new PLACE_FLOORITEM(fishingArea));
        //}
    }

    @Override
    public void onPlayerRemoved(Player player) {

    }

    public static boolean isSupported(String modelName) {
        return "park_a".equals(modelName);
    }
}
