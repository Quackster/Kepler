package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class MESSENGERSREADY extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("MESSENGERSREADY");
	}

}
