package org.alexdev.roseau.messages.outgoing;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.player.PlayerDetails;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.settings.RoomType;
import org.alexdev.roseau.log.DateTime;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class MEMBERINFO extends OutgoingMessageComposer {

	private PlayerDetails details;

	public MEMBERINFO(PlayerDetails details) {
		this.details = details;
	}

	@Override
	public void write() {
		response.init("MEMBERINFO");
		response.appendArgument("");
		response.appendNewArgument(details.getName());
		response.appendNewArgument(details.getPersonalGreeting());

		Player player = Roseau.getGame().getPlayerManager().getByName(this.details.getName());

		if (player == null) {
			response.appendNewArgument(DateTime.formatDateTime(this.details.getLastOnline()));
			response.appendNewArgument(""); // shows 'offline'
		} else {
			
			response.appendNewArgument(DateTime.formatDateTime());

			boolean hotelView = true;
			Room room = null;
			
			if (player.getPrivateRoomPlayer() != null) {

				room = player.getPrivateRoomPlayer().getRoomUser().getRoom();

				if (room != null) {
					hotelView = false;
				}
			} 
			
			if (player.getPublicRoomPlayer() != null) {

				room = player.getPublicRoomPlayer().getRoomUser().getRoom();

				if (room != null) {
					hotelView = false;
				}
			}
			
			if (!hotelView) {
				
				if (room.getData().getRoomType() == RoomType.PRIVATE) {
					response.appendNewArgument("In a user flat");
				} 
				
				if (room.getData().getRoomType() == RoomType.PUBLIC) {
					response.appendNewArgument(room.getData().getName());
				}
				
			} else {
				response.appendNewArgument("On Hotel View");
			}
		}

		response.appendNewArgument(details.getFigure());
	}

}
