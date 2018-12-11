package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class OK extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("OK");
	}

}
