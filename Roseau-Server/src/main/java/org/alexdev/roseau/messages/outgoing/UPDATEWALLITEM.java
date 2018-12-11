package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class UPDATEWALLITEM extends OutgoingMessageComposer {

	private Item item;

	public UPDATEWALLITEM(Item item) {
		this.item = item;
	}

	@Override
	public void write() {
		response.init("UPDATEITEM");
		response.append(Character.toString((char)13));
		response.appendObject(this.item);
	}

}
