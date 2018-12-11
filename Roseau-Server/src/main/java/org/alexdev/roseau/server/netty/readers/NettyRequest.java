package org.alexdev.roseau.server.netty.readers;

import org.alexdev.roseau.server.messages.ClientMessage;

public class NettyRequest implements ClientMessage {

	private String header;
	private String content;

	public NettyRequest(String header, String content) {
		this.header = header;
		this.content = content;
	}
	
	@Override
	public String getHeader() {
		return header;
	}

	@Override
	public String getMessageBody() {
		String consoleText = new String(this.content);

		for (int i = 0; i < 13; i++) { 
			consoleText = consoleText.replace(Character.toString((char)i), "[" + i + "]");
		}

		return consoleText;
	}
	
	public int getArgumentAmount() {
		return this.getArgumentAmount(" ");
	}
	
	public int getArgumentAmount(String delimeter) {
		return this.content.split(delimeter).length;
	}

	@Override
	public String getArgument(int index) {
		return this.getArgument(index, " ");
	}

	@Override
	public String getArgument(int index, String delimeter) {
		return this.content.split(delimeter)[index];
	}

}


