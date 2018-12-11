package org.alexdev.roseau.messages.incoming;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.navigator.NavigatorRequest;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.BUSY_FLAT_RESULTS;
import org.alexdev.roseau.server.messages.ClientMessage;

public class SEARCHFLATFORUSER implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		String username = reader.getArgument(1, "/");
		
		Player requestedPlayer = Roseau.getGame().getPlayerManager().getByName(username);
		
		if (requestedPlayer != null) {
			
			List<Room> rooms = requestedPlayer.getRooms();
			player.send(new BUSY_FLAT_RESULTS(rooms, NavigatorRequest.PRIVATE_ROOMS));
		}
	}

}
