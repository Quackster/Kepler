package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MOVESTUFF implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		int itemID = Integer.valueOf(reader.getArgument(0));
		int x = Integer.valueOf(reader.getArgument(1));
		int y = Integer.valueOf(reader.getArgument(2));

		Room room = player.getRoomUser().getRoom();
		player.getRoomUser().resetAfkTimer();

		if (room == null) {
			return;
		}

		if (!room.hasRights(player, false) && !room.getData().hasAllSuperUser()) {
			return;
		}

		Item item = room.getItem(itemID);

		if (item == null) {
			return;
		}

		item.getPosition().setX(x);
		item.getPosition().setY(y);

		boolean rotation_only = false;

		if (reader.getArgumentAmount() > 3) {
			int rotation = Integer.valueOf(reader.getArgument(3));

			if (rotation != item.getPosition().getRotation()) {
				item.getPosition().setRotation(rotation);
				rotation_only = true;
			}
		}

		room.getMapping().updateItemPosition(item, rotation_only);
	}

}
