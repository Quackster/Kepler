package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.navigator.NavigatorRequest;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.BUSY_FLAT_RESULTS;
import org.alexdev.roseau.server.messages.ClientMessage;

public class SEARCHFLAT implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		String query = reader.getMessageBody().substring(2);
		
		if (!(query.length() > 1)) {
			return;
		}
		
		query = query.substring(0, query.length() - 1);
		
		player.send(new BUSY_FLAT_RESULTS(Roseau.getDao().getNavigator().getRoomsByLikeName(query), NavigatorRequest.SEARCH_ROOMS));
	}

}
