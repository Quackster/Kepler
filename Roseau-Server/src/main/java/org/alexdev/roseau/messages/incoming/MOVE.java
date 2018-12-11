package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class MOVE implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (reader.getArgumentAmount() < 2) {
			return;
		}

		int x = Integer.valueOf(reader.getArgument(0));
		int y = Integer.valueOf(reader.getArgument(1));
		
		player.getRoomUser().walkTo(x, y);
		
		/*if (x == 9 && y == 8) {
			player.send(new OBJECTS_WORLD("lobby"));
		}*/
	}

}
