package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class PH_NOTICKETS extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("PH_NOTICKETS");
	}

}
