package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class MESSENGERREADY extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("MESSENGERREADY");
	}

}
