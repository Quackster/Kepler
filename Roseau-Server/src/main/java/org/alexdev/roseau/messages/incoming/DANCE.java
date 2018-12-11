package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class DANCE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		if (player.getRoomUser().getRoom() == null) {
			return;
		}
		
		player.getRoomUser().setStatus("dance", "", true, -1);
		player.getRoomUser().setNeedUpdate(true);
	}

}
