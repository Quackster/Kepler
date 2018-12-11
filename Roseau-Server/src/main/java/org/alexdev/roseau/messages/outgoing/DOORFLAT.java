package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;

public class DOORFLAT extends OutgoingMessageComposer {
	
	private int itemID;
	private int roomID;

	public DOORFLAT(int itemID, int roomID) {
		this.itemID = itemID;
		this.roomID = roomID;
	}

	@Override
	public void write() {

		response.init("DOORFLAT");
		response.appendNewArgument(String.valueOf(this.itemID));
		response.appendNewArgument(String.valueOf(this.roomID));
	}
}
