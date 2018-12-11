package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class ENCRYPTION_OFF extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("ENCRYPTION_OFF");
	}
}
