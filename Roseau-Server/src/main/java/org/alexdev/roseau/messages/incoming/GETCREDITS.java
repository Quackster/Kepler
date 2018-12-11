package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.MESSENGERSMSACCOUNT;
import org.alexdev.roseau.messages.outgoing.MESSENGERSREADY;
import org.alexdev.roseau.messages.outgoing.WALLETBALANCE;
import org.alexdev.roseau.server.messages.ClientMessage;

public class GETCREDITS implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {	
		player.send(new WALLETBALANCE(player.getDetails().getCredits()));
		player.send(new MESSENGERSMSACCOUNT());
		player.send(new MESSENGERSREADY());
	}

}
