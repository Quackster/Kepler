package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;

public class ACTIVEOBJECT_REMOVE extends OutgoingMessageComposer {

	private Item item;

	public ACTIVEOBJECT_REMOVE(Item item) {
		this.item = item;
	}

	@Override
	public void write() {
		response.init("ACTIVEOBJECT_REMOVE");
		response.appendNewArgument(this.item.getPadding());
		response.append(String.valueOf(this.item.getID()));
	}
}
