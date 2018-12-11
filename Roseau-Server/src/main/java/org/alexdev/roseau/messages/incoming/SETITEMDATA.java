package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class SETITEMDATA implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		int itemID = Integer.valueOf(reader.getArgument(1, "/"));
		String stickyData = reader.getMessageBody().replace("/" + itemID + "/", "");

		Room room = player.getRoomUser().getRoom();
		
		if (room == null) {
			return;
		}
		
		if (!room.hasRights(player, false)) {
			return;
		}
		
		Item item = room.getItem(itemID);
		
		if (item == null) {
			return;
		}
		
		if (!stickyData.startsWith(item.getCustomData())) {
			Log.println("Scripting, maybe? ITEM ID " + itemID);
			return;
		}
		
		item.setCustomData(stickyData);
		item.save();
	}

}
