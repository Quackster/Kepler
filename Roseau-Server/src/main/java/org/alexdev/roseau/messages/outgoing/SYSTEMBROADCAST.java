package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class SYSTEMBROADCAST extends OutgoingMessageComposer {

	private String message;

	public SYSTEMBROADCAST(String message) {
		this.message = message;
	}

	@Override
	public void write() {
		response.init("SYSTEMBROADCAST");
		response.appendNewArgument(this.message);
	}

}
