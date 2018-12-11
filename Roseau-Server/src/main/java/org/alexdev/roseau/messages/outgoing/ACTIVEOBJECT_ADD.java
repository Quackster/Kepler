package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class ACTIVEOBJECT_ADD extends OutgoingMessageComposer {

	private Item item;

	public ACTIVEOBJECT_ADD(Item item) {
		this.item = item;
	}

	@Override
	public void write() {
		response.init("ACTIVEOBJECT_ADD");
		response.appendObject(this.item);
	}

}
