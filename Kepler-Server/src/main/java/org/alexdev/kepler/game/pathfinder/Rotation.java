package org.alexdev.kepler.game.pathfinder;

public class Rotation {
    public static int calculateHumanDirection(int x1, int y1, int X2, int Y2) {
        int rotation = 0;

        if (x1 > X2 && y1 > Y2)
            rotation = 7;
        else if (x1 < X2 && y1 < Y2)
            rotation = 3;
        else if (x1 > X2 && y1 < Y2)
            rotation = 5;
        else if (x1 < X2 && y1 > Y2)
            rotation = 1;
        else if (x1 > X2)
            rotation = 6;
        else if (x1 < X2)
            rotation = 2;
        else if (y1 < Y2)
            rotation = 4;

        return rotation;
    }

    public static int calculateWalkDirection(Position from, Position to) {
        return calculateWalkDirection(from.getX(), from.getY(), to.getX(), to.getY());
    }

    public static int calculateWalkDirection(int x, int y, int to_x, int to_y) {
        if (x == to_x) {
            if (y < to_y)
                return 4;
            else
                return 0;
        } else if (x > to_x) {
            if (y == to_y)
                return 6;
            else if (y < to_y)
                return 5;
            else
                return 7;
        } else {
            if (y == to_y)
                return 2;
            else if (y < to_y)
                return 3;
            else
                return 1;
        }
    }

    public static int getHeadRotation(int rotation, Position position, Position towards) {
        int headRotation = rotation;
        int diff = rotation - Rotation.calculateHumanDirection(position.getX(), position.getY(), towards.getX(), towards.getY());

        if ((position.getRotation() % 2) == 0) {
            if (diff > 0) {
                headRotation = (rotation - 1);
            } else if (diff < 0) {
                headRotation = (rotation + 1);
            }
        }

        return headRotation;
    }

}
