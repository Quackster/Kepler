package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class ERROR extends OutgoingMessageComposer {

	private String errorMessage;

	public ERROR(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public void write() {
		response.init("ERROR");
		response.appendArgument(this.errorMessage);
	}

}
