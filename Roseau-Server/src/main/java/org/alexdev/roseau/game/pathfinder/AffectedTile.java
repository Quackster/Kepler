package org.alexdev.roseau.game.pathfinder;

import java.util.List;

import org.alexdev.roseau.game.room.model.Position;

import com.google.common.collect.Lists;

public class AffectedTile {
	
	/*
	 * Credits to Mike and Nillus for this, this was 
	 * taken from Blunk V5 because the same bullshit code from Uber and other crappy emulators didn't work!
	 */
	public static List<Position> getAffectedTilesAt(int length, int width, int posX, int posY, int rotation) {
		List<Position> tiles = Lists.newArrayList();
		
		// Is this a non-square item?
		if (length != width) {
			
			// Flip rotation
			if (rotation == 0 || rotation == 4) {
				int tmpL = length;
				length = width;
				width = tmpL;
			}
		}
		
		for (int iX = posX; iX < posX + width; iX++) {
			for (int iY = posY; iY < posY + length; iY++) {
				tiles.add(new Position(iX, iY));
			}
		}
		
		return tiles;
	}

}