package org.alexdev.roseau.messages.outgoing;

import java.util.Arrays;
import java.util.List;

import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.entity.EntityType;
import org.alexdev.roseau.game.room.Room;
import org.alexdev.roseau.messages.OutgoingMessageComposer;


public class USERS extends OutgoingMessageComposer {

	private List<Entity> entities;
	
	public USERS(Entity entity) {
		this.entities = Arrays.asList(new Entity[] { entity });
	}
	
	public USERS(List<Entity> entities) {
		this.entities = entities;
	}

	@Override
	public void write() {
		response.init("USERS");
		for (Entity entity : this.entities) {
			response.append(Character.toString((char)13));
			response.appendArgument("");
			response.appendArgument(entity.getDetails().getName());
			response.appendArgument(entity.getDetails().getFigure());
			response.appendArgument(String.valueOf(entity.getRoomUser().getPosition().getX()));
			response.appendArgument(String.valueOf(entity.getRoomUser().getPosition().getY()));
			response.appendArgument(String.valueOf(entity.getRoomUser().getPosition().getZ()));
			response.appendArgument(entity.getDetails().getMission());
			//response.appendArgument("ch=s02/53,51,44");
			
			Room room = entity.getRoomUser().getRoom();
			
			if (room.getData().getModel().hasPool() && entity.getType() == EntityType.PLAYER) {
				response.appendArgument(entity.getDetails().getPoolFigure());
			}
		}
	}

}
