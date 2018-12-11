package org.alexdev.roseau.game.room.model;

public class Rotation {
	
	public static byte calculateDirection(int X, int Y, int toX, int toY)
	{
		byte result = 0;
        if (X > toX && Y > toY)
        {
            result = 7;
        }
        else if (X < toX && Y < toY)
        {
            result = 3;
        }
        else if (X > toX && Y < toY)
        {
            result = 5;
        }
        else if (X < toX && Y > toY)
        {
            result = 1;
        }
        else if (X > toX)
        {
            result = 6;
        }
        else if (X < toX)
        {
            result = 2;
        }
        else if (Y < toY)
        {
            result = 4;
        }
        else if (Y > toY)
        {
            result = 0;
        }

        return result;
	}
}
