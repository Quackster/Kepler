package org.alexdev.kepler.messages.incoming.rooms.user;

import org.alexdev.kepler.game.games.gamehalls.GamehallGame;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.entities.RoomEntity;
import org.alexdev.kepler.game.games.triggers.GameTrigger;
import org.alexdev.kepler.messages.types.MessageEvent;
import org.alexdev.kepler.server.netty.streams.NettyRequest;

public class IIM implements MessageEvent {
    @Override
    public void handle(Player player, NettyRequest reader) throws Exception {
        String contents = reader.contents();
        String[] commandArgs = reader.contents().split(" ");

        RoomEntity roomEntity = player.getRoomUser();

        if (roomEntity.getRoom() == null) {
            return;
        }

        Room room = roomEntity.getRoom();
        Item currentItem = roomEntity.getCurrentItem();

        // If we're on a current item and the current item has a valid trigger
        if (currentItem == null) {
            return;
        }

        // If the trigger isn't a game trigger then ignore it
        if (currentItem.getItemTrigger() == null || !(currentItem.getItemTrigger() instanceof GameTrigger)) {
            return;
        }

        GameTrigger trigger = (GameTrigger) currentItem.getItemTrigger();
        GamehallGame game = trigger.getGameInstance(roomEntity.getPosition());

        if (game == null) {
            return;
        }

        String gameId = commandArgs[0];
        String command = commandArgs[1];

        if (!gameId.equals(game.getGameId())) {
            return;
        }

        String arguments = contents.replace(gameId + " " + command, "");

        if (arguments.startsWith(" ")) {
            arguments = arguments.trim();
        }

        game.handleCommand(player, room, player.getRoomUser().getCurrentItem(), command, arguments.split(" "));
    }
}
