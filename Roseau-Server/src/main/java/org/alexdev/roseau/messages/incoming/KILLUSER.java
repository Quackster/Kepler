package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class KILLUSER implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		String name = reader.getMessageBody();

		Room room = player.getRoomUser().getRoom();

		player.getRoomUser().resetAfkTimer();

		if (!room.hasRights(player, false)) {
			return;
		}

		Player killed = room.getPlayerByName(name);

		if (killed == null) {
			return;
		}

		if (killed.hasPermission("room_kick_any_user")) {
			
			// If the person you're trying to kick can kick any users, you also need the same permission to kick 'em
			if (!player.hasPermission("room_kick_any_user")) {
				return;
			}
		}

		killed.kick();

	}

}
