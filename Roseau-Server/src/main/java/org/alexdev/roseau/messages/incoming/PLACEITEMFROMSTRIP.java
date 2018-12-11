package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class PLACEITEMFROMSTRIP implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		int itemID = Integer.valueOf(reader.getArgument(0));
		
		Item item = player.getInventory().getItem(itemID);
		player.getRoomUser().resetAfkTimer();
		
		if (item == null) {
			return;
		}
		
		Room room = player.getRoomUser().getRoom();
		
		if (room == null) {
			return;
		}
		
		if (!room.hasRights(player, false) && !room.getData().hasAllSuperUser()) {
			return;
		}

		if (!item.getDefinition().getBehaviour().isOnWall()) {
			return;
		}
		
		String wallPosition = reader.getMessageBody().replace(itemID + " ", "");
		wallPosition = wallPosition.replace("/" + item.getCustomData(), "");
		
		item.setWallPosition(wallPosition);
		item.setOwnerID(room.getData().getOwnerID());
		item.save();
		
		//item.delete(); // delete the item because they dont display anyways
		
		// dont bother adding the item the room
		room.getMapping().addItem(item);
		player.getInventory().removeItem(item);
	}

}
       