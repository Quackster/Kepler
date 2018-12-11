package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.FLATPROPERTY;
import org.alexdev.roseau.server.messages.ClientMessage;

public class FLATPROPERTYBYITEM implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		String decoration = reader.getArgument(1, "/");
		int itemID = Integer.valueOf(reader.getArgument(2, "/"));

		Room room = player.getRoomUser().getRoom();

		if (room == null) {
			return;
		}

		if (!room.hasRights(player, false)) {
			return;
		}

		Item item = player.getInventory().getItem(itemID);

		if (item == null) {
			return;
		}
		
		if (!item.getDefinition().getBehaviour().isDecoration()) {
			return;
		}

		if (decoration.equals("wallpaper")) {
			room.getData().setWall(item.getCustomData());
		} else if  (decoration.equals("floor")) { 
			room.getData().setFloor(item.getCustomData());
		}
	
		player.getInventory().removeItem(item);
		
		item.delete();
		
		room.send(new FLATPROPERTY(decoration, item.getCustomData()));
		room.getData().save();
	}

}
