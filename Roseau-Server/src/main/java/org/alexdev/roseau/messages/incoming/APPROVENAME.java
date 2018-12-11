package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.NAME_APPROVED;
import org.alexdev.roseau.messages.outgoing.NAME_UNACCEPTABLE;
import org.alexdev.roseau.server.messages.ClientMessage;

public class APPROVENAME implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (reader.getArgumentAmount() > 0) {

			String name = reader.getArgument(0);

			if (name.length() > 0) {
				if (REGISTER.approveName(name)) {
					player.send(new NAME_APPROVED());
				} else {
					player.send(new NAME_UNACCEPTABLE());
				}
			}

		}
	}

}
