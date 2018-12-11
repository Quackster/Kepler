package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class PURCHASE_ADDSTRIPITEM extends OutgoingMessageComposer {

	@Override
	public void write() {
		response.init("ADDSTRIPITEM");
	}

}
