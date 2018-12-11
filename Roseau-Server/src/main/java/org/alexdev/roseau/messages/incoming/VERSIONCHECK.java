package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.ENCRYPTION_OFF;
import org.alexdev.roseau.messages.outgoing.SECRET_KEY;
import org.alexdev.roseau.server.messages.ClientMessage;

public class VERSIONCHECK implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		player.send(new ENCRYPTION_OFF());
		player.send(new SECRET_KEY("31vw2swky25q9ko940i8x068ftxrmt0wa3vgj27qtrr3m35rn067o549fl"));
	}

}
