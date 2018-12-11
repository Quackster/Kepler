package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class SETSTUFFDATA implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		int itemID = Integer.valueOf(reader.getArgument(1, "/"));
		String dataClass = reader.getArgument(2, "/");
		String customData = reader.getArgument(3, "/");
		
		RoomUser roomUser = player.getRoomUser();
		Item item = roomUser.getRoom().getItem(itemID);
		
		if (item == null) {
			return;
		}
		
		roomUser.getRoom().getMapping().updateItem(player, item, dataClass, customData);
	}

}
