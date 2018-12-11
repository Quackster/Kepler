package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class REMOVEITEM implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		int itemID = Integer.valueOf(reader.getArgument(1, "/"));
		
		Room room = player.getRoomUser().getRoom();
		player.getRoomUser().resetAfkTimer();
		
		if (!room.hasRights(player, true)) {
			return;
		}
		
		Item item = room.getItem(itemID);

		if (item == null) {
			return;
		}
		
		room.getMapping().removeItem(item);
		
		item.delete();
	}

}
