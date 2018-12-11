package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_DECLINEBUDDY implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		String username = reader.getMessageBody();
		
		if (username == null) {
			return;
		}

		int fromID = Roseau.getDao().getPlayer().getId(username);
		
		if (fromID < 1) {
			return;
		}
		
		if (fromID == player.getDetails().getID()) {
			return;
		}

		if (!player.getMessenger().hasRequest(fromID)) {
			return;
		}

		int toID = player.getDetails().getID();
		
		Roseau.getDao().getMessenger().removeRequest(fromID, toID);

	}

}
