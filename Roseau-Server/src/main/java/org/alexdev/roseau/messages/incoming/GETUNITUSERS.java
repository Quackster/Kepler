package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.ALLUNITS;
import org.alexdev.roseau.messages.outgoing.UNITMEMBERS;
import org.alexdev.roseau.server.messages.ClientMessage;

public class GETUNITUSERS implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		Room publicRoom = Roseau.getGame().getRoomManager().getRoomByName(reader.getArgument(1, "/"));
		
		if (publicRoom == null) {
			return;
		}
		
		player.send(new ALLUNITS(Roseau.getGame().getRoomManager().getPublicRooms()));
		player.send(new UNITMEMBERS(publicRoom.getEntities(EntityType.PLAYER)));
	}

}
