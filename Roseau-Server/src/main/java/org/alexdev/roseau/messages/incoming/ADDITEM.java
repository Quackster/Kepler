package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class ADDITEM implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		/*[04/23/2017 16:05:47] [ROSEAU] >> [-1927607359] Received: ADDITEM /post.it/frontwall -2.8438,-8.4444,2844/FFFF31 dddddddd
[04/23/2017 16:05:47] [ROSEAU] >> [-1927607359] Received: SETSTRIPITEMDATA 
232
19*/
		Room room = player.getRoomUser().getRoom();
		
		player.getRoomUser().resetAfkTimer();

		if (!room.hasRights(player, false)) {
			return;
		}
		
		String sprite = reader.getArgument(1, "/");
		String wallPosition = reader.getArgument(2, "/");
		String extraData = reader.getArgument(3, "/");
		
		boolean hasValidSticky = false;
		int definitionId = -1;
		
		for (Item item : player.getInventory().getItems()) {
			if (item.getDefinition().getSprite().equals(sprite)) {
				definitionId = item.getDefinition().getID();
				hasValidSticky = true;
			}
		}
		
		if (!hasValidSticky) {
			return;
		}
		
		Item item = Roseau.getDao().getInventory().newItem(definitionId, player.getDetails().getID(), extraData);
		item.setRoomID(room.getData().getID());
		item.setWallPosition(wallPosition);
		
		room.getMapping().addItem(item);
	}

}
