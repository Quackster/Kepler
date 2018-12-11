package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class REMOVEWALLITEM extends OutgoingMessageComposer {

	private int id;

	public REMOVEWALLITEM(int i) {
		this.id = i;
	}

	@Override
	public void write() {
		response.init("REMOVEITEM");
		response.appendNewArgument(String.valueOf(this.id));
	}

}
