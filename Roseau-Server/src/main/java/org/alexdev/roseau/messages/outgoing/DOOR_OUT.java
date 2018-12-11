package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.messages.OutgoingMessageComposer;

public class DOOR_OUT  extends OutgoingMessageComposer {

	private Item item;
	private String player;
	
	public DOOR_OUT(Item item, String player) {
		this.item = item;
		this.player = player;
	}

	@Override
	public void write() {
		response.init("DOOR_OUT");
		response.appendNewArgument(this.item.getPadding());
		response.append(Integer.toString(this.item.getID()));
		response.appendPartArgument(this.player);
	}

}
