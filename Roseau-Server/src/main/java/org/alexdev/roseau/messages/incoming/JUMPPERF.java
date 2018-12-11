package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.JUMPDATA;
import org.alexdev.roseau.server.messages.ClientMessage;

public class JUMPPERF implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		Room room = player.getRoomUser().getRoom();
		Item item = player.getRoomUser().getCurrentItem();

		if (room != null && item != null && item.getDefinition().getSprite().equals("poolLift")) {
			String data = reader.getArgument(3, Character.toString((char)13));
			room.send(new JUMPDATA(player.getDetails().getName(), data));
		}
	}

}
