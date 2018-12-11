package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;
import org.alexdev.roseau.util.Util;

public class UPDATEFLAT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		// TODO Auto-generated method stub
		
		
		/*[04/07/2017 19:18:56] [ROSEAU] >> [-2121264494] Received: UPDATEFLAT / /42/dddddddddddd/open/0
[04/07/2017 19:18:56] [ROSEAU] >> [-2121264494] Received: SETFLATINFO / /42/description=vedww
password=123
allsuperuser=1
wordfilter_disable=*/
		
		Room room = Roseau.getGame().getRoomManager().getRoomByID(Integer.valueOf(reader.getArgument(1, "/")));
		
		if (room == null) {
			return;
		}
		
		if (!room.hasRights(player, true)) {
			return;
		}
		
		
		String roomName = Util.filterInput(reader.getArgument(2, "/"));
		String roomState = reader.getArgument(3, "/");
		boolean showOwnerName = reader.getArgument(4, "/").equals("1");
		
		if (!(roomName.length() > 2)) {
			roomName = room.getData().getName();
		}
		
		int state = 0;

		if (roomState.equals("closed")) {
			state = 1;
		}

		if (roomState.equals("password")) {
			state = 2;
		}

		room.getData().setName(roomName);
		room.getData().setState(state);
		room.getData().setShowOwnerName(showOwnerName);
		room.getData().save();

	}

}
