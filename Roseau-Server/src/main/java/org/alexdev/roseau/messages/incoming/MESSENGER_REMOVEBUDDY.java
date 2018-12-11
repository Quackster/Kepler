package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.messenger.MessengerUser;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MESSENGER_REMOVEBUDDY implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		String username = reader.getMessageBody();
		
		if (username == null) {
			return;
		}

		int friendID = Roseau.getDao().getPlayer().getId(username);
		
		if (friendID < 1) {
			return;
		}
		
		if (friendID == player.getDetails().getID()) {
			return;
		}

		if (!player.getMessenger().isFriend(friendID)) {
			return;
		}

		int toID = player.getDetails().getID();
		Roseau.getDao().getMessenger().removeFriend(friendID, toID);
		
		MessengerUser friend = player.getMessenger().getFriend(friendID);
		
		if (friend.isOnline()) {
			friend.getPlayer().getMessenger().removeFriend(player.getDetails().getID());
			friend.getPlayer().getMessenger().sendFriends();
		}	
		
		player.getMessenger().removeFriend(friendID);
		player.getMessenger().sendFriends();
	}

}
