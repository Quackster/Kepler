package org.alexdev.roseau.game.messenger;

public class MessengerMessage {

	private int ID;
	private int toID;
	private int fromID;
	private long timeSent;
	private String message;
	
	public MessengerMessage(int ID, int toID, int fromID, long timeSent, String message) {
		this.ID = ID;
		this.toID = toID;
		this.fromID = fromID;
		this.timeSent = timeSent;
		this.message = message;
	}

	public int getID() {
		return ID;
	}

	public int getToID() {
		return toID;
	}

	public int getFromID() {
		return fromID;
	}

	public long getTimeSent() {
		return timeSent;
	}

	public String getMessage() {
		return message;
	}
	
}
