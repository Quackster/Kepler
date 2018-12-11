package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.server.messages.ClientMessage;

public class CRYFORHELP implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		///Private Room: returd;0;ngga plz;39[9]returd[9]Alex[9]open[9][9]floor1[9]127.0.0.1[9]127.0.0.1[9]90[9]0[9]null[9]


		Room room = player.getRoomUser().getRoom();

		if (room == null) {
			return;
		}

		// Ugly way to get the distress message, but whatever


		String distressMessage = null;

		if (room.getData().getRoomType() == RoomType.PRIVATE) {
			distressMessage = reader.getArgument(0, Character.toString((char)9));
			distressMessage = distressMessage.replace("/Private Room: " + room.getData().getName(), "").substring(3);
			distressMessage = distressMessage.replace(";" + room.getData().getID(), "");
		} else {
			distressMessage = reader.getMessageBody();
			distressMessage = distressMessage.replace("/" + room.getData().getName(), "").substring(3);
			distressMessage = distressMessage.split(";")[0];
		}
		
		Roseau.getGame().getModerationManager().callForHelp(room, player, distressMessage);
	}
}
