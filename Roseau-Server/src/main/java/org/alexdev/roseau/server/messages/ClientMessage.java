package org.alexdev.roseau.server.messages;

public interface ClientMessage {

	public String getHeader();
	public String getMessageBody();
	
	public int getArgumentAmount();
	public int getArgumentAmount(String delimeter);
	
	public String getArgument(int index);
	public String getArgument(int index, String delimeter);
}
