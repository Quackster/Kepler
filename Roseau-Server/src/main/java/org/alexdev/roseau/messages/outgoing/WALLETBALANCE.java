package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class WALLETBALANCE extends OutgoingMessageComposer {

	private int credits;
	
	public WALLETBALANCE(int credits) {
		this.credits = credits;
	}

	@Override
	public void write() {
		response.init("WALLETBALANCE");
		response.appendNewArgument(String.valueOf(this.credits));
	}

}
