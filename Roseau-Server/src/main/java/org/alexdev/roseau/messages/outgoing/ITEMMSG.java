package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;

public class ITEMMSG extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("ITEMMSG 0");
		response.appendNewArgument("SELECTTYPE x");
	}

}
