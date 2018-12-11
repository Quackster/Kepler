package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class PLACESTUFFFROMSTRIP implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		int itemID = Integer.valueOf(reader.getArgument(0));
		int x = Integer.valueOf(reader.getArgument(1));
		int y = Integer.valueOf(reader.getArgument(2));
		int rotation = 0;//Integer.valueOf(reader.getArgument(5));
		
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

		if (!item.getDefinition().getBehaviour().isOnFloor()) {
			return;
		}
		
		if (item.getDefinition().getBehaviour().isTeleporter()) {
			rotation = 4;
		}
		
		item.setOwnerID(room.getData().getOwnerID());
		item.getPosition().setX(x);
		item.getPosition().setY(y);
		item.getPosition().setRotation(rotation);
		
		room.getMapping().addItem(item);
		
		player.getInventory().removeItem(item);
		//player.getInventory().refresh();
	}

}
