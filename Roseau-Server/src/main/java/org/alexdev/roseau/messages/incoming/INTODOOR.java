package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class INTODOOR implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		int itemID = Integer.valueOf(reader.getMessageBody());

		Room room = player.getRoomUser().getRoom();

		if (room == null) {
			return;
		}
		
		Item item = room.getItem(itemID);

		if (item == null) {
			return;
		}

		if (!item.getDefinition().getBehaviour().isTeleporter()) {
			return;
		}

		Position pos = player.getRoomUser().getPosition();

		if (((item.getPosition().getRotation() == 0 || item.getPosition().getRotation() == 2) && ((pos.getX() == item.getPosition().getX() + 1) && 
			(pos.getY() == item.getPosition().getY()))) || 
			(item.getPosition().getRotation() == 4 && ((pos.getX() == item.getPosition().getX()) && (pos.getY() == item.getPosition().getY() + 1))))
		{
			player.getRoomUser().walkTo(item.getPosition().getX(), item.getPosition().getY());
		}
	}

}
