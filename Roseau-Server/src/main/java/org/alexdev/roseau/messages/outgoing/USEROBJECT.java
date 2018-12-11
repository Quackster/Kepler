package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class USEROBJECT extends OutgoingMessageComposer {

	private PlayerDetails details;

	public USEROBJECT(PlayerDetails details) {
		this.details = details;
	}

	@Override
	public void write() {
		response.init("USEROBJECT");
		response.appendObject(this.details);
	}

}
