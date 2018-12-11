package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class GOAWAY implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		Room room = player.getRoomUser().getRoom();

		if (room == null) {
			return;
		}

		Position near = player.getRoomUser().getPosition();

		if (!near.isMatch(room.getData().getModel().getDoorPosition())) {
			if (player.getRoomUser().walkTo(room.getData().getModel().getDoorPosition().getX(), room.getData().getModel().getDoorPosition().getY())) {
				player.getRoomUser().setKickWhenStop(true);
				//player.getRoomUser().setCanWalk(false);
				return;
			}
		}
		
		player.kick();

		//player.dispose();
		//player.kick();
	}

}
