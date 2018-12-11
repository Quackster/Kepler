package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class REMOVERIGHTS implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		Room room = player.getRoomUser().getRoom();
		
		if (!room.hasRights(player, true)) {
			return;
		}
		
		Player removeRights = room.getPlayerByName(reader.getMessageBody());
		room.removeUserRights(removeRights);

	}

}
