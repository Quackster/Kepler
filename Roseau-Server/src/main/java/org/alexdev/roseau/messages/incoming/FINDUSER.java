package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.MEMBERINFO;
import org.alexdev.roseau.messages.outgoing.NOSUCHUSER;
import org.alexdev.roseau.server.messages.ClientMessage;

public class FINDUSER implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		boolean foundUser = false;

		if (reader.getMessageBody().length() > 0) {

			String[] args = reader.getMessageBody().split("\t", 2);
			String name = args[0];

			if (name.length() > 0) {
				if (Roseau.getDao().getPlayer().isNameTaken(name)) {
					foundUser = true;
					player.send(new MEMBERINFO(Roseau.getDao().getPlayer().getDetails(name)));
				}		
			}
		}
		
		if (!foundUser) {
			player.send(new NOSUCHUSER());
		}
	}

}
