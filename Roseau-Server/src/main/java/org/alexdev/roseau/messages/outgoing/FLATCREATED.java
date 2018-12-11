
package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class FLATCREATED extends OutgoingMessageComposer {

	private Room room;

	public FLATCREATED(Room room) {
		this.room = room;
	}

	@Override
	public void write() {
		response.init("FLATCREATED");
		response.appendNewArgument(Integer.toString(room.getData().getID()));
		response.appendArgument(room.getData().getName());

	}

}
