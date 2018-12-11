/*
 * Copyright (c) 2012 Quackster <alex.daniel.97@gmail>. 
 * 
 * This file is part of Sierra.
 * 
 * Sierra is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Sierra is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Sierra.  If not, see <http ://www.gnu.org/licenses/>.
 */

package org.alexdev.roseau.game.room.model;

import org.alexdev.roseau.log.Log;

public class RoomModel 
{
	public final static int OPEN = 0;
	public final static int CLOSED = 1;

	private String name;
	private String heightmap;
	private String[][] squareChar;

	private int doorX;
	private int doorY;
	private int doorZ;
	private int doorRot;
	private int mapSizeX;
	private int mapSizeY;

	private int[][] squares;
	private double[][] squareHeight;
	private boolean hasPool;
	private boolean disabledHeightCheck;

	public RoomModel(String name, String heightmap, int doorX, int doorY, int doorZ, int doorRot, boolean hasPool, boolean heightCheck) {

		try {
			this.name = name;
			this.heightmap = heightmap.replace(Character.toString((char)13), "").replace(Character.toString((char)10), "").replace(" ", Character.toString((char)13));
			this.doorX = doorX;
			this.doorY = doorY;
			this.doorZ = doorZ;
			this.doorRot = doorRot;

			String[] temporary = heightmap.split(" ");

			this.mapSizeX = temporary[0].length();
			this.mapSizeY = temporary.length;
			this.squares = new int[mapSizeX][mapSizeY];
			this.squareHeight = new double[mapSizeX][mapSizeY];
			this.squareChar = new String[mapSizeX][mapSizeY];
			this.hasPool = hasPool;
			this.disabledHeightCheck = heightCheck;

			for (int y = 0; y < mapSizeY; y++) {

				if (y > 0) {
					temporary[y] = temporary[y];
				}

				for (int x = 0; x < mapSizeX; x++) {

					//Log.println(temporary[y].substring(x,  x +1));

					String square = temporary[y].substring(x,x + 1).trim().toLowerCase();

					if (square.toLowerCase().equals("x"))	{
						squares[x][y] = CLOSED;

					} else if(isNumeric(square)) {

						squares[x][y] = OPEN;
						squareHeight[x][y] = Double.parseDouble(square);
					}


					if (this.doorX == x && this.doorY == y) {
						squares[x][y] = OPEN;
						squareHeight[x][y] = Double.parseDouble(this.doorZ + "");
					}

					squareChar[x][y] = square;

					// This should edited into the heightmap itself but I cba
					if (this.name.equals("pub_a")) {
						if (x == 9 && y == 9) {
							squares[x][y] = CLOSED;
						}
						
						if (x == 11 && y == 1) {
							squares[x][y] = CLOSED;
						}
					}

					if (this.name.equals("pool_a")) {
						if (x == 6 && y == 31) {
							squares[x][y] = CLOSED;
						}
						
						if (x == 6 && y == 32) {
							squares[x][y] = CLOSED;
						}
					}

				}


			}
		} catch (Exception e) {
			Log.println("Error parsing room model: " + this.name);
			e.printStackTrace();
		}

	}

	public double getHeight(Position point) {
		return squareHeight[point.getX()][point.getY()];
	}


	public String getHeightMap() {
		return heightmap;
	}

	private boolean isNumeric(String input) {

		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getName() {
		return name;
	}

	public int getDoorX() {
		return doorX;
	}

	public int getDoorY() {
		return doorY;
	}

	public int getDoorZ() {
		return doorZ;
	}

	public int getDoorRot() {
		return doorRot;
	}

	public int getMapSizeX() {
		return mapSizeX;
	}

	public int getMapSizeY() {
		return mapSizeY;
	}

	public double getHeight(int x, int y) {

		if (this.invalidXYCoords(x, y)) {
			return 0;
		}

		return squareHeight[x][y];
	}

	public boolean isBlocked(int x, int y) {

		if (this.invalidXYCoords(x, y)) {
			return true;
		}

		return squares[x][y] == RoomModel.CLOSED;
	}

	public boolean invalidXYCoords(int x, int y) {
		if (x >= this.mapSizeX) {
			return true;
		}

		if (y >= this.mapSizeY) {
			return true;
		}

		if (x < 0) {
			return true;
		}

		if (y < 0) {
			return true;
		}

		return false;
	}

	public String[][] getSquareChar() {
		return squareChar;
	}

	public Position getDoorPosition() {
		return new Position(this.doorX, this.doorY, this.doorZ);
	}

	public boolean hasPool() {
		return hasPool;
	}

	public boolean hasDisabledHeightCheck() {
		return disabledHeightCheck;
	}
}