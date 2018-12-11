package org.alexdev.roseau.game.item.interactors;

import java.util.concurrent.TimeUnit;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.GameVariables;
import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.messages.outgoing.DOOR_IN;
import org.alexdev.roseau.messages.outgoing.DOOR_OUT;

public class TeleporterInteractor extends Interaction {

	public TeleporterInteractor(Item item) {
		super(item);
	}

	@Override
	public void onTrigger(Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStoppedWalking(Player player) {

		Item teleporter = player.getRoomUser().getRoom().getItem(item.getTargetTeleporterID());

		if (teleporter == null) {
			teleporter = Roseau.getDao().getItem().getItem(item.getTargetTeleporterID());
		}

		final Item targetTeleporter = teleporter;
		final Room targetRoom = Roseau.getDao().getRoom().getRoom(targetTeleporter.getRoomID(), true);
		final Room previousRoom = player.getRoomUser().getRoom();

		if (targetRoom != null) {

			player.getRoomUser().setCanWalk(false);
			player.getRoomUser().getRoom().send(new DOOR_OUT(item, player.getDetails().getName()));

			final Item currentTeleporter = this.item;

			Runnable task = new Runnable() {
				@Override
				public void run() {

					if (currentTeleporter.getRoomID() != targetTeleporter.getRoomID()) {

						if (previousRoom != null) {
							previousRoom.leaveRoom(player, false);
						}

						targetRoom.loadRoom(player, targetTeleporter.getPosition(), targetTeleporter.getPosition().getRotation());

					} else {
						player.getRoomUser().getPosition().set(targetTeleporter.getPosition());
						player.getRoomUser().sendStatusComposer();
						
						TeleporterInteractor interactor = (TeleporterInteractor)targetTeleporter.getInteraction();
						interactor.leaveTeleporter(player);
					}
				}
			};

			Roseau.getGame().getScheduler().schedule(task, GameVariables.TELEPORTER_DELAY, TimeUnit.MILLISECONDS);
		} else {
			Log.println("error");
		}

	}
	
	public void leaveTeleporter(final Player player) {

		if (!this.definition.getBehaviour().isTeleporter()) {
			return;
		}

		if (player.getRoomUser().getRoom() == null) {
			return;
		}

		player.getRoomUser().setCanWalk(false);
		player.getRoomUser().getRoom().send(new DOOR_IN(this.item, player.getDetails().getName()));

		final Item item = this.item;

		Runnable task = new Runnable() {
			@Override
			public void run() {

				item.setCustomData("TRUE");
				item.updateStatus();

				player.getRoomUser().setCanWalk(true);
				player.getRoomUser().walkTo(item.getPosition().getSquareInFront().getX(), item.getPosition().getSquareInFront().getY());
			}
		};

		Roseau.getGame().getScheduler().schedule(task, GameVariables.TELEPORTER_DELAY, TimeUnit.MILLISECONDS);
	}

}
