package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.game.moderation.CallForHelp;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class CRYFORHELP extends OutgoingMessageComposer {

	private CallForHelp cfh;

	public CRYFORHELP(CallForHelp cfh) {
		this.cfh = cfh;
	}

	@Override
	public void write() {
		response.init("CRYFORHELP");
		response.appendObject(this.cfh);
	}
}
