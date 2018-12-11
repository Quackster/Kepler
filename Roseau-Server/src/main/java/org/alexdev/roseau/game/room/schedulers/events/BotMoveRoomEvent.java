package org.alexdev.roseau.game.room.schedulers.events;

import java.util.List;

import org.alexdev.roseau.game.player.Bot;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.schedulers.RoomEvent;
import org.alexdev.roseau.util.Util;

public class BotMoveRoomEvent extends RoomEvent {

	public BotMoveRoomEvent(Room room) {
		super(room);
	}

	@Override
	public void tick() {

		if (this.room.getBots().size() < 1) {
			return;
		}

		for (Bot bot : this.room.getBots()) {

			RoomUser roomUser = bot.getRoomUser();

			List<Player> nearbyPlayers = this.room.getMapping().getNearbyPlayers(bot, bot.getStartPosition(), 3);

			if (nearbyPlayers.size() > 0) {

				if (!roomUser.getPosition().isMatch(bot.getStartPosition())) {
					if (!roomUser.isWalking()) {

						if (this.canTick(10)) { // 5 seconds
							roomUser.walkTo(bot.getStartPosition().getX(), bot.getStartPosition().getY());
						}
					}
				} else {
					if (!roomUser.isWalking()) {
						if (roomUser.getPosition().getRotation() != bot.getStartPosition().getRotation()) {
							roomUser.getPosition().setRotation(bot.getStartPosition().getRotation());
							roomUser.setNeedUpdate(true);
						}
					}
				}
			} else {

				if (this.canTick(10)) { // 5 seconds

					if (bot.getPositions().size() > 0) {
						int[] position = bot.getPositions().get(Util.getRandom().nextInt(bot.getPositions().size() - 1));
						bot.getRoomUser().walkTo(position[0], position[1]);
					}
				}
			}
		}

		this.increaseTicked();
	}

}
