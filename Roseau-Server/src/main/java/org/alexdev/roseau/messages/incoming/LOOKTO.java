package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.game.room.model.Rotation;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class LOOKTO implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		int x = Integer.valueOf(reader.getArgument(0));
		int y = Integer.valueOf(reader.getArgument(1));

		player.getRoomUser().resetAfkTimer();
		
		if (player.getRoomUser().containsStatus("lay")) {
			return;
		}

		if (player.getRoomUser().containsStatus("sit")) {
			return;
		}

		if (player.getRoomUser().getPosition().isMatch(new Position(x, y))) {
			return;
		}

		if (player.getRoomUser().isWalking()) {
			return;
		}

		int rotation = Rotation.calculateDirection(
				player.getRoomUser().getPosition().getX(), 
				player.getRoomUser().getPosition().getY(), 
				x, 
				y);

		if (rotation != player.getRoomUser().getPosition().getRotation()) {

			player.getRoomUser().getPosition().setRotation(rotation);
			player.getRoomUser().setNeedUpdate(true);
		}

	}

}
