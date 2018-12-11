package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class ROOM_READY extends OutgoingMessageComposer {

	private String description;

	public ROOM_READY(String description) {
		this.description = description;
	}

	@Override
	public void write() {
		response.init("ROOM_READY");
		response.appendNewArgument(this.description);
	}

}
