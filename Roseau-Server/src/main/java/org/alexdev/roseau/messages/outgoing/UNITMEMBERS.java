package org.alexdev.roseau.messages.outgoing;

import java.util.List;

import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class UNITMEMBERS extends OutgoingMessageComposer {

	private List<Entity> entities;

	public UNITMEMBERS(List<Entity> entities) {
		this.entities = entities;
	}

	@Override
	public void write() {
		response.init("UNITMEMBERS");
		
		for (Entity entity : this.entities) {
			response.appendNewArgument(entity.getDetails().getName());
		}
	}

}
