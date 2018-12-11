package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class LOGOUT extends OutgoingMessageComposer {

	private String username;

	public LOGOUT(String username) {
		this.username = username;
	}

	@Override
	public void write() {
		response.init("LOGOUT");
		response.appendNewArgument(this.username);
	}

}
