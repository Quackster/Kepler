package org.alexdev.roseau.game.moderation;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.server.messages.Response;
import org.alexdev.roseau.server.messages.SerializableObject;

public class CallForHelp implements SerializableObject {

	private Room room;
	private Player from;
	private String message;
	private String time;
	
	public CallForHelp(Room room, Player from, String message) {
		this.room = room;
		this.from = from;
		this.message = message;
		this.time = DateTime.formatDateTime();
		
		//-- ["url": ["cryinguser": "Private Room: Main Lobby", "url": "url", "CryMsg": "efwfewfewfewfwfewf", "Unit": "Message (from: test): in Lobby", "gDoor": "0", "PickedCry": "<nobody>", "CryPrivate": ""]]
	}
	
	@Override
	public void serialise(Response msg) {
		msg.appendNewArgument("Private Room: " + this.room.getData().getName() + " @ " + this.time);
		msg.appendNewArgument("url");
		msg.appendNewArgument("From: " + this.from.getDetails().getName() + ";0;Message: " + this.message);
		
		
	}
	
	public Room getRoom() {
		return room;
	}

	public Player getFrom() {
		return from;
	}

	public String getMessage() {
		return message;
	}



}
