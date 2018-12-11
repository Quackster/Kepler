package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class CHAT extends OutgoingMessageComposer {

	private String header;
	private String talkMessage;
	private String username;

	public CHAT(String header, String username, String talkMessage) {
		this.header = header;
		this.username = username;
		this.talkMessage = talkMessage;
	}

	@Override
	public void write() {

		response.init(this.header);
		response.appendNewArgument(this.username);
		response.appendArgument(this.talkMessage);
	}

}
