package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.OK;
import org.alexdev.roseau.server.messages.ClientMessage;

public class STATUSOK implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		player.send(new OK());
	}

}
