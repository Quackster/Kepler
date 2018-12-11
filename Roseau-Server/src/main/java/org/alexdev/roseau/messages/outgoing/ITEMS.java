package org.alexdev.roseau.messages.outgoing;

import java.util.List;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class ITEMS extends OutgoingMessageComposer {

	private Room room;

	public ITEMS(Room room) {
		this.room = room;
	}

	@Override
	public void write() {
		response.init("ITEMS");
		response.append(Character.toString((char)13));

		int i = 0;
		
		List<Item> wallItems = room.getWallItems();

		for (Item item : wallItems) {

			i++;

			response.appendObject(item);

			if (i > 0 && i != wallItems.size()) {
				response.append("\\");
			}
		}
	}
}
