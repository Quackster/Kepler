package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class SIGN implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		Room room = player.getRoomUser().getRoom();
		
		if (room == null) {
			return;
		}
		
		if (!room.getData().getModelName().equals("pool_b")) {
			return;
		}
		
		// Get ID of sign
		int num = Integer.parseInt(reader.getMessageBody());
		
		if (num >= 1 && num <= 14)
		{
			// Diving score: 4 ... 10 [1 = 4, 7 = 10]
			if(num >= 1 && num <= 7)
			{
				// TODO: account 'vote on dive'
			}
	
			// Add status
			player.getRoomUser().removeStatus("dance");
			player.getRoomUser().setStatus("sign", " " + Integer.toString(num), false, 2, true);
		}

	}

}
