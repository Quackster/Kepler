package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class JUMPDATA extends OutgoingMessageComposer {

	private String name;
	private String data;

	public JUMPDATA(String name, String data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public void write() {
		response.init("JUMPDATA");
		response.appendNewArgument(this.name);
		response.appendNewArgument(this.data);
		
	}

}
