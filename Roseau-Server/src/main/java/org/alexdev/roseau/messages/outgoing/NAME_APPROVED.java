package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class NAME_APPROVED extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("NAME_APPROVED");
	}

}
