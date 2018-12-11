package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class FLATINFO extends OutgoingMessageComposer {

	private Room room;

	public FLATINFO(Room room) {
		this.room = room;
	}

	@Override
	public void write() {
		response.init("SETFLATINFO");
		response.appendNewArgument("/");
		response.append(String.valueOf(this.room.getData().getID()));
		response.append("/");
	}

}
