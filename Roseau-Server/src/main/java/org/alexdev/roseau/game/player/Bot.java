package org.alexdev.roseau.game.player;

import org.alexdev.roseau.game.entity.EntityType;

import java.util.List;

import org.alexdev.roseau.game.entity.Entity;
import org.alexdev.roseau.game.room.entity.RoomUser;
import org.alexdev.roseau.game.room.model.Position;
import org.alexdev.roseau.log.Log;
import org.alexdev.roseau.util.Util;

public class Bot implements Entity {

	private PlayerDetails details;
	private RoomUser roomEntity;

	private Position startPosition;

	private List<int[]> positions;
	private List<String> responses;
	private List<String> triggers;


	public Bot(Position position, List<int[]> positions, List<String> responses, List<String> triggers) {
		this.details = new PlayerDetails(this);
		this.roomEntity = new RoomUser(this);

		this.positions = positions;
		this.startPosition = position;
		this.responses = responses;
		this.triggers = triggers;
		
		for (String response : responses) {
			Log.println(response);
		}
	}
	
	public String containsTrigger(String phrase) {
		
		for (String trigger : this.triggers) {
			if (phrase.toLowerCase().contains(trigger.toLowerCase())) {
				return trigger;
			}
		}
		
		return null;
	}

	public String getResponse(String username, String item) {
		
		if (this.responses.size() > 0) {
			String response = this.responses.get(Util.getRandom().nextInt(this.responses.size()));
			
			String newResponse = response;
			newResponse = newResponse.replace("%username%", username);
			newResponse = newResponse.replace("%item%", item);
			
			return newResponse;
		}
		
		return null;
	}

	public PlayerDetails getDetails() {
		return details;
	}

	@Override
	public EntityType getType() {
		return EntityType.BOT;
	}

	@Override
	public RoomUser getRoomUser() {
		return this.roomEntity;
	}

	public Position getStartPosition() {
		return startPosition;
	}

	public List<int[]> getPositions() {
		return positions;
	}
}
