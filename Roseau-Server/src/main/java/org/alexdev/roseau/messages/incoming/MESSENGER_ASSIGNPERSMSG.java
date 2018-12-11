package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;
import org.alexdev.roseau.util.Util;

public class MESSENGER_ASSIGNPERSMSG implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		String persistentMessage = Util.filterInput(reader.getMessageBody());
		
		if (persistentMessage.length() > 23) {
			persistentMessage = persistentMessage.substring(0, 21);
		}
		
		player.getDetails().setPersonalGreeting(persistentMessage);
		player.getDetails().save();

	}

}
