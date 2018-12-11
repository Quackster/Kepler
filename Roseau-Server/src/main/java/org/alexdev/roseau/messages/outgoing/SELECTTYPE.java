package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;

public class SELECTTYPE extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("SELECTTYPE");
		response.appendNewArgument("x");
	}

}
