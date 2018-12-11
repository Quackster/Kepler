package org.alexdev.roseau.game;

import java.net.InetAddress;
import java.util.List;
import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.log.Log;

public class GameScheduler implements Runnable {

	private long tickRate = 0;
	
	@Override
	public void run() {
		try {
			List<Player> players = Roseau.getGame().getPlayerManager().getMainServerPlayers();
			

			if ((this.tickRate % GameVariables.CREDITS_EVERY_SECS) == 0) {
				for (int i = 0; i < players.size(); i++) {
					Player player = players.get(i);
					
					player.getDetails().setCredits(player.getDetails().getCredits() + GameVariables.CREDITS_EVERY_AMOUNT);
					player.getDetails().sendCredits();
					player.getDetails().save();
				}
			}
			
			if ((this.tickRate % 300) == 0) {
				if (!Roseau.hasValidIpAddress(Roseau.getRawConfigIP())) {
				Roseau.setServerIP(InetAddress.getByName(Roseau.getRawConfigIP()).getHostAddress());
				}
				
			}
			
			for (int i = 0; i < players.size(); i++) {
				
				Player player = players.get(i);
				
				Player roomHandlePlayer = null;
				
				if (player.getPrivateRoomPlayer() != null) {
					roomHandlePlayer = player.getPrivateRoomPlayer();
				}
				
				if (player.getPublicRoomPlayer() != null) {
					roomHandlePlayer = player.getPublicRoomPlayer();
				}
				
				if (roomHandlePlayer != null) {
					
					RoomUser roomUser = roomHandlePlayer.getRoomUser();
					
					if (roomUser.getAfkTimer() > 0) {
						roomUser.setAfkTimer(roomUser.getAfkTimer() - 1);
					} else {
						if (roomUser.getAfkTimer() == 0) {
							roomHandlePlayer.kick();
						}
					}
				}
			}
			
			/*if ((this.tickRate % 60) == 0) {
				
				for (int i = 0; i < Roseau.getGame().getRoomManager().getLoadedRooms().values().size(); i++) {
					
					Room room = Roseau.getGame().getRoomManager().getLoadedRooms().;
					
					if (room.getData().getRoomType() == RoomType.PUBLIC) {
						continue;
					}
					
					if (room.getData() == null) {
						room.dispose(true);
						continue;
					}
					
					Player player = Roseau.getGame().getPlayerManager().getByID(room.getData().getId());
					
					if (player == null) {
						room.dispose(true);
						continue;
					}
				}
			}*/
			
			
		} catch (Exception e) {
			Log.exception(e);
		}
		
		this.tickRate++;
	}

}
