package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class ADDSTRIPITEM implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		player.getRoomUser().resetAfkTimer();
		
		int itemID = Integer.valueOf(reader.getArgument(2));

		Room room = player.getRoomUser().getRoom();

		if (!room.hasRights(player, true)) {
			return;
		}

		Item item = room.getItem(itemID);

		if (item == null) {
			return;
		}

		if (item.getDefinition().getBehaviour().isOnFloor()) {
			item.getPosition().setX(-1);
			item.getPosition().setY(-1);
			item.setOwnerID(player.getDetails().getID());
			item.updateEntities();
		}

		room.getMapping().removeItem(item);

		player.getInventory().addItem(item);
		player.getInventory().refresh("last");
	}

}
