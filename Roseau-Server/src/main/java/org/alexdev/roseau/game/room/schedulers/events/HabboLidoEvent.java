package org.alexdev.roseau.game.room.schedulers.events;

import java.util.List;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.game.room.schedulers.RoomEvent;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;
import org.alexdev.roseau.util.Util;

public class HabboLidoEvent extends RoomEvent {

	private int followingID = -1;
	private int cameraType = -1;

	public HabboLidoEvent(Room room) {
		super(room);
	}

	@Override
	public void tick() {

		try {
			if (this.room.getPlayerByID(this.followingID) == null) {
				this.findNewTarget();
			}

			if (this.canTick(9)) {

				int cameraEffect = Util.getRandom().nextInt(3);

				if (cameraEffect == 0) {
					this.findNewTarget();
				}

				if (cameraEffect == 1) {

					if (this.cameraType != 1) {
						this.cameraType = 1;
						this.room.send(new SHOWPROGRAM(new String[] {"cam1", "setcamera", Integer.toString(this.cameraType) }));	
					}
				}

				if (cameraEffect == 2) {
					if (this.cameraType != 2) {
						this.cameraType = 2;
						this.room.send(new SHOWPROGRAM(new String[] {"cam1", "setcamera", Integer.toString(this.cameraType) }));	
					}
				}
			}
			
			for (Player player : this.room.getPlayers()) {

				RoomUser roomUser = player.getRoomUser();
						
				Item item = this.room.getMapping().getHighestItem(roomUser.getPosition().getX(), roomUser.getPosition().getY());

				if (item != null) {
					if (item.getDefinition().getSprite().equals("poolQueue")) {
						if (!roomUser.isWalking()) {
							
							Position next = new Position(item.getCustomData());
							roomUser.walkTo(next.getX(), next.getY());
						}
					}
				}
			}
			
		} catch (Exception e) { }

		this.increaseTicked();
	}

	private void findNewTarget() {

		List<Player> players = room.getPlayers();

		if (players.isEmpty()) {
			return;
		}

		Player player = null;

		if (players.size() == 1) {
			player = players.get(0);
		} else {
			player = players.get(Util.getRandom().nextInt(players.size()));
		}

		/*if (this.followingID != player.getDetails().getId()) {*/
		this.followingID = player.getDetails().getID();
		this.room.send(new SHOWPROGRAM(new String[] {"cam1", "targetcamera", player.getDetails().getName() }));
		//}
	}

}
