package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class PH_TICKETS extends OutgoingMessageComposer {

	private int tickets;

	public PH_TICKETS(int tickets) {
		this.tickets = tickets;
	}

	@Override
	public void write() {
		response.init("PH_TICKETS");
		response.appendArgument(Integer.toString(this.tickets));
	}

}
