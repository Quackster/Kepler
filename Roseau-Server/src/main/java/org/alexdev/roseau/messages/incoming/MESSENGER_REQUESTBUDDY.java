package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.messenger.MessengerUser;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_REQUESTBUDDY implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		String username = reader.getArgument(0, Character.toString((char)13));
		
		if (username == null) {
			return;
		}

		int toID = Roseau.getDao().getPlayer().getId(username);
		
		if (toID < 1) {
			return;
		}
		
		if (toID == player.getDetails().getID()) {
			return;
		}

		if (player.getMessenger().hasRequest(toID)) {
			return;
		}

		if (Roseau.getDao().getMessenger().newRequest(player.getDetails().getID(), toID)) {

			MessengerUser user = new MessengerUser(toID);
			player.getMessenger().getRequests().add(user);

			if (user.isOnline()) {
				user.getPlayer().getMessenger().getRequests().add(new MessengerUser(player.getDetails().getID()));
				user.getPlayer().getMessenger().sendRequests();
			}
		}

	}

}
