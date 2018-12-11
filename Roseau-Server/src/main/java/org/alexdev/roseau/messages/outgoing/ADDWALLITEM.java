package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class ADDWALLITEM extends OutgoingMessageComposer {

	private Item item;

	public ADDWALLITEM(Item item) {
		this.item = item;
	}

	@Override
	public void write() {
		response.init("ADDITEM");
		response.append(Character.toString((char)13));
		response.appendObject(this.item);
	}

}
