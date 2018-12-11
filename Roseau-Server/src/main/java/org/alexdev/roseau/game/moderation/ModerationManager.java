package org.alexdev.roseau.game.moderation;

import java.util.List;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.outgoing.CRYFORHELP;

public class ModerationManager {
	public void callForHelp(Room room, Player player, String distressMessage) {
		List<Player> players = Roseau.getGame().getPlayerManager().getMainServerPlayers();
		
		for (Player moderator : players) {
			
			if (!moderator.hasPermission("answer_call_for_help")) {
				continue;
			}
			
			CallForHelp cfh = new CallForHelp(room, player, distressMessage.replace(";", ","));
			moderator.send(new CRYFORHELP(cfh));
		}
	}
}
