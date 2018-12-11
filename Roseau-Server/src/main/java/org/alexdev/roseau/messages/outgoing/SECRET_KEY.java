package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class SECRET_KEY extends OutgoingMessageComposer {

	private String key;
	
	public SECRET_KEY(String key) {
		super();
		this.key = key;
	}

	@Override
	public void write() {
		response.init("SECRET_KEY");
		response.appendNewArgument(key);
	}
}
