package org.alexdev.roseau.game.item.interactors.furniture;

import java.util.List;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.Interaction;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.game.room.model.Position;

import com.google.common.collect.Lists;

public class BedInteractor extends Interaction {

	public BedInteractor(Item item) {
		super(item);
	}

	@Override
	public void onTrigger(Player player) { 	}

	@Override
	public void onStoppedWalking(Player player) {

		if (this.isValidPillowTile(player.getRoomUser().getPosition())) {

			player.getRoomUser().getPosition().setRotation(item.getPosition().getRotation());
			player.getRoomUser().removeStatus("dance");
			player.getRoomUser().removeStatus("lay");
			player.getRoomUser().setStatus("lay", " " + Double.toString(definition.getHeight() + 1.5) + " null", true, -1);
			
		} else {

			for (Position tile : this.getValidPillowTiles()) {

				if (player.getRoomUser().getPosition().getX() != tile.getX()) {
					player.getRoomUser().getPosition().setY(tile.getY());
				}

				if (player.getRoomUser().getPosition().getY() != tile.getY()) {
					player.getRoomUser().getPosition().setX(tile.getX());
				}
			}
			
			player.getRoomUser().currentItemTrigger();
			
		}
	}

	public boolean isValidPillowTile(Position position) {

		if (this.definition.getBehaviour().isCanLayOnTop()) {

			if (item.getPosition().getX() == position.getX() && item.getPosition().getY() == position.getY()) {
				return true;
			} else {

				int validPillowX = -1;
				int validPillowY = -1;

				if (item.getPosition().getRotation() == 0) {
					validPillowX = item.getPosition().getX() + 1;
					validPillowY = item.getPosition().getY();
				}

				if (item.getPosition().getRotation() == 2) {
					validPillowX = item.getPosition().getX();
					validPillowY = item.getPosition().getY() + 1;
				}

				if (validPillowX == position.getX() && validPillowY == position.getY()) {
					return true;
				}
			}
		}

		return false;
	}

	public List<Position> getValidPillowTiles() {

		List<Position> tiles = Lists.newArrayList();
		tiles.add(new Position(item.getPosition().getX(), item.getPosition().getY()));

		int validPillowX = -1;
		int validPillowY = -1;

		if (item.getPosition().getRotation() == 0) {
			validPillowX = item.getPosition().getX() + 1;
			validPillowY = item.getPosition().getY();
		}

		if (item.getPosition().getRotation() == 2) {
			validPillowX = item.getPosition().getX();
			validPillowY = item.getPosition().getY() + 1;
		}

		tiles.add(new Position(validPillowX, validPillowY));

		return tiles;
	}
}
