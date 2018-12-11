package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class DELETEFLAT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		Room room = Roseau.getGame().getRoomManager().getRoomByID(Integer.valueOf(reader.getArgument(1, "/")));
		
		if (!room.hasRights(player, true)) {
			return;
		}
		
		for (Item item : room.getItems().values()) {
			item.delete();
		}
		
		room.dispose(true);
		
		Roseau.getDao().getRoom().deleteRoom(room);
	}

}
