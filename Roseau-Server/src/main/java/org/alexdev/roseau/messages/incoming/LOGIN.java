package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.SYSTEMBROADCAST;
import org.alexdev.roseau.server.messages.ClientMessage;

public class LOGIN implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {

		if (!(reader.getArgumentAmount() > 1)) {
			player.send(new SYSTEMBROADCAST("Your username or password was incorrect."));
			player.getNetwork().close();
			return;
		}
		
		String username = reader.getArgument(0);
		String password = reader.getArgument(1);
		
		boolean authenticated = Roseau.getDao().getPlayer().login(player, username, password);

		if (authenticated) {
			
			Player otherPlayer = Roseau.getGame().getPlayerManager().getPlayerByPortDifferentConnection(player.getDetails().getID(), player.getNetwork().getServerPort(), player.getNetwork().getConnectionId());
			
			if (otherPlayer != null) {
				otherPlayer.getNetwork().close();
			}
			
			player.getDetails().setAuthenticated(true);
			player.getDetails().setPassword(password);
			
			if (reader.getArgumentAmount() > 2) {
				Room room = Roseau.getGame().getRoomManager().getRoomByPort(player.getNetwork().getServerPort());
				
				if (room == null) {
					
					// Since public rooms need to bind to their own port, I've made it so
					// the ID of the public room is just the current players connected port minus the main server IP
					// eg; 30045 - 30001 = public room ID 44
					// 
					// pree simple m8
					//
					int publicRoomID = player.getNetwork().getServerPort() - Roseau.getServerPort();
					
					room = Roseau.getDao().getRoom().getRoom(publicRoomID, true);
					
					if (room == null) {
						Log.println("Grabbed new room from database: " + publicRoomID);
						return;
					}
				}
				
				
				room.loadRoom(player);
			}
			
			player.login(!(reader.getArgumentAmount() > 2));
			
			
		} else {
			player.send(new SYSTEMBROADCAST("Your username or password was incorrect."));
			player.getNetwork().close();
			return;
		}
	}
}
