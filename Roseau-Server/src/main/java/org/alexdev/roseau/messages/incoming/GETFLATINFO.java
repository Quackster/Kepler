package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.FLATINFO;
import org.alexdev.roseau.server.messages.ClientMessage;

public class GETFLATINFO implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		Room room = Roseau.getGame().getRoomManager().getRoomByID(Integer.valueOf(reader.getArgument(1, "/")));
		
		if (room == null) {
			return;
		}
		
		player.send(new FLATINFO(room));
	}

}
