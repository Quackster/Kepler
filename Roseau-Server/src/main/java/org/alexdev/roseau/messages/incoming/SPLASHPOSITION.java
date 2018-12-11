package org.alexdev.roseau.messages.incoming;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.pool.PoolLiftInteractor;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.messages.MessageEvent;
import org.alexdev.roseau.messages.outgoing.SHOWPROGRAM;
import org.alexdev.roseau.server.messages.ClientMessage;

public class SPLASHPOSITION implements MessageEvent {

	@Override
	public void handle(Player player, ClientMessage reader) {
		
		Room room = player.getRoomUser().getRoom();
		Item item = player.getRoomUser().getCurrentItem();

		if (room != null && item != null && item.getDefinition().getSprite().equals("poolLift")) {
			
			Position position = new Position(reader.getMessageBody());

			room.send(new SHOWPROGRAM(new String[] { "BIGSPLASH", "POSITION", position.toString() }));
			
			player.getRoomUser().getPosition().set(position);
			player.getRoomUser().setStatus("swim", "", true, -1, true);
			
			player.getRoomUser().setCanWalk(true);
			player.getRoomUser().walkTo(18, 19);
			
			PoolLiftInteractor interactor = (PoolLiftInteractor)item.getInteraction();
			interactor.open();
			
		} else {
			player.kickAllConnections();
		}
		
		/*		// Get user and tile object
		SpaceUser usr = comm.getSpaceInstance().getUserByClientID(comm.clientID);
		Item obj = comm.getSpaceInstance().getInteractor().getPassiveObjectOnTile(usr.X, usr.Y);
		
		// User is indeed diving?
		if (usr != null && obj != null && obj.definition.sprite.equals("poolLift"))
		{
			// Determine 'landing position' of jump and position of pool exit
			SpaceTile position = SpaceTile.parse(msg.getBody());
			SpaceTile exit = SpaceTile.parse(obj.customData);
			
			// Display splash for clients
			ServerMessage notify = new ServerMessage("SHOWPROGRAM");
			notify.appendArgument("BIGSPLASH");
			notify.appendArgument("POSITION");
			notify.appendArgument(position.toString());
			comm.getSpaceInstance().broadcast(notify);
			
			// Locate user in pool on landing position
			usr.addStatus("swim", null, 0, null, 0, 0);
			comm.getSpaceInstance().getInteractor().warpUser(usr, position.X, position.Y, true);
			
			// Start moving to pool exit
			comm.getSpaceInstance().getInteractor().startUserMovement(usr, exit.X, exit.Y, false);
			
			// Door is open again!
			comm.getSpaceInstance().showProgram(obj.itemData, "open");
		}*/

	}

}
