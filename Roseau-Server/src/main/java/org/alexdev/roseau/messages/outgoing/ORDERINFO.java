package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class ORDERINFO extends OutgoingMessageComposer {

	private String name;
	private int credits;

	public ORDERINFO(String name, int credits) {
		this.name = name;
		this.credits = credits;
	}

	@Override
	public void write() {
		response.init("ORDERINFO");
		response.appendNewArgument(name);
		response.appendNewArgument(String.valueOf(this.credits));
	}

}
