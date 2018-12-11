package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class ACTIVEOBJECT_UPDATE extends OutgoingMessageComposer {

	private Item item;

	public ACTIVEOBJECT_UPDATE(Item item) {
		this.item = item;
	}

	@Override
	public void write() {
		response.init("ACTIVEOBJECT_UPDATE");

		if (this.item != null) {
			response.appendObject(item);
		}
	}
}
