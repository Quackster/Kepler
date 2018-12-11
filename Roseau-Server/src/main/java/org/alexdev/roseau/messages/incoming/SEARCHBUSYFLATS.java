package org.alexdev.roseau.messages.incoming;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.navigator.NavigatorRequest;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.BUSY_FLAT_RESULTS;
import org.alexdev.roseau.server.messages.ClientMessage;

import com.google.common.collect.Lists;

public class SEARCHBUSYFLATS implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {


		if (reader.getMessageBody().length() > 0) {

			String[] parts = reader.getMessageBody().replace("/", "").split(",");
			int multipler = Integer.valueOf(parts[0]);

			List<Room> rooms = Roseau.getGame().getRoomManager().getPopularRooms(multipler);

			if (rooms != null) {
				player.send(new BUSY_FLAT_RESULTS(rooms, NavigatorRequest.POPULAR_ROOMS));
				return;
			}
		}

		player.send(new BUSY_FLAT_RESULTS(Lists.newArrayList(), NavigatorRequest.POPULAR_ROOMS));
	}

}
