package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.FLAT_LETIN;
import org.alexdev.roseau.server.messages.ClientMessage;

public class LETUSERIN implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		Room room = player.getRoomUser().getRoom();

		if (room == null) {
			return;
		}

		if (!room.hasRights(player, false)) {
			return;
		}
		
		Player let = Roseau.getGame().getPlayerManager().getPrivateRoomPlayer(reader.getMessageBody());
		
		if (let == null) {
			return;
		}
		
		let.getRoomUser().setRoom(room);
		let.send(new FLAT_LETIN());
	}

}
