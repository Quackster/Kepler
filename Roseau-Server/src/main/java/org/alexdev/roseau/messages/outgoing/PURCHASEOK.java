package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class PURCHASEOK extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("PURCHASE_OK");

	}

}
