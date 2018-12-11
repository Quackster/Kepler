package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.FLATCREATED;
import org.alexdev.roseau.server.messages.ClientMessage;
import org.alexdev.roseau.util.Util;

public class CREATEFLAT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		String floor = reader.getArgument(1, "/");
		String roomName = Util.filterInput(reader.getArgument(2, "/"));
		String roomModel = reader.getArgument(3, "/");
		String roomState = reader.getArgument(4, "/");
		boolean showOwnerName = reader.getArgument(5, "/").equals("1");
		
		Player publicRoomPlayer = player.getPublicRoomPlayer();

		if (publicRoomPlayer != null) {
			publicRoomPlayer.getNetwork().close();
		}

		if (!floor.equals("first floor")) {
			player.kickAllConnections();
			return;
		}
		
		if (!(roomName.length() > 2)) {
			
			player.sendAlert("The room name needs to be at least 3 characters long");
			return;
		}
		

		int state = 0;

		if (roomState.equals("closed")) {
			state = 1;
		}

		if (roomState.equals("password")) {
			state = 2;
		}

		if (!roomModel.equals("model_a") && 
				!roomModel.equals("model_b") && 
				!roomModel.equals("model_c") && 
				!roomModel.equals("model_d") && 
				!roomModel.equals("model_e") && 
				!roomModel.equals("model_f")) {

			// Possibru scripter? HAX! KICK THEM!!!
			player.kickAllConnections();
			return;
		}

		Room room = Roseau.getDao().getRoom().createRoom(player, roomName, "", roomModel, state, showOwnerName);
		player.setLastCreatedRoom(room);

		player.send(new FLATCREATED(room));

	}

}
