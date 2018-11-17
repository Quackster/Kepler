package org.alexdev.kepler.game.pathfinder;

import java.util.ArrayList;
import java.util.List;

public class Position {

    private int X;
    private int Y;
    private double Z;
    private int bodyRotation;
    private int headRotation;

    public Position() {
        this(0, 0, 0);
    }

    public Position(int x, int y) {
        this.X = x;
        this.Y = y;
        this.Z = 0;
    }

    public Position(int x, int y, double z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }
    
    public Position(int x, int y, double z, int headRotation, int bodyRotation) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.headRotation = headRotation;
        this.bodyRotation = bodyRotation;
    }

    /**
     * Gets the height difference between another height variable
     *
     * @param differentZ the other height
     * @return the height difference
     */
    public static double getHeightDifference(double firstZ, double differentZ) {
        
        double bigger;
        double smaller;
        
        if (differentZ > firstZ) {
            bigger = differentZ;
            smaller = firstZ;
        } else {
            bigger = firstZ;
            smaller = differentZ;
        }
        
        return (bigger - smaller);
    }

    /**
     * Gets the x.
     *
     * @return the x
     */
    public int getX() {
        return X;
    }

    /**
     * Sets the x.
     *
     * @param x the new x
     */
    public void setX(int x) {
        X = x;
    }

    /**
     * Gets the y.
     *
     * @return the y
     */
    public int getY() {
        return Y;
    }

    /**
     * Sets the y.
     *
     * @param y the new y
     */
    public void setY(int y) {
        Y = y;
    }

    /**
     * Gets the z.
     *
     * @return the z
     */
    public double getZ() {
        return Z;
    }

    /**
     * Sets the z.
     *
     * @param z the new z
     */
    public void setZ(double z) {
        Z = z;
    }

    /**
     * Gets the body rotation.
     *
     * @return the body rotation
     */
    public int getBodyRotation() {
        return bodyRotation;
    }

    /**
     * Sets the body rotation.
     *
     * @param bodyRotation the new body rotation
     */
    public void setBodyRotation(int bodyRotation) {
        this.bodyRotation = bodyRotation;
    }

    /**
     * Gets the head rotation.
     *
     * @return the head rotation
     */
    public int getHeadRotation() {
        return headRotation;
    }

    /**
     * Sets the head rotation.
     *
     * @param headRotation the new head rotation
     */
    public void setHeadRotation(int headRotation) {
        this.headRotation = headRotation;
    }

    /**
     * Gets the rotation.
     *
     * @return the rotation
     */
    public int getRotation() {
        return bodyRotation;
    }

    /**
     * Sets the rotation.
     *
     * @param headRotation the new rotation
     */
    public void setRotation(int headRotation) {
        this.headRotation = headRotation;
        this.bodyRotation = headRotation;
    }

    /**
     * Adds the.
     *
     * @param other the other
     * @return the position
     */
    public Position add(Position other) {
        return new Position(other.getX() + getX(), other.getY() + getY(), other.getZ() + getZ());
    }

    /**
     * Subtract.
     *
     * @param other the other
     * @return the position
     */
    public Position subtract(Position other) {
        return new Position(other.getX() - getX(), other.getY() - getY(), other.getZ() - getZ());
    }

    /**
     * Gets the distance squared.
     *
     * @param point the point
     * @return the distance squared
     */
    public int getDistanceSquared(Position point) {
        int dx = this.getX() - point.getX();
        int dy = this.getY() - point.getY();

        return (dx * dx) + (dy * dy);
    }
    
    /**
     * Gets the square in front.
     *
     * @return the square in front
     */
    public Position getSquareInFront() {
        Position square = this.copy();

        if (this.bodyRotation == 0) {
            square.Y--;
        } else if (this.bodyRotation == 1) {
            square.X++;
            square.Y--;
        } else if (this.bodyRotation == 2) {
            square.X++;
        } else if (this.bodyRotation == 3) {
            square.X++;
            square.Y++;
        }  else if (this.bodyRotation == 4) {
            square.Y++;
        } else if (this.bodyRotation == 5) {
            square.X--;
            square.Y++;
        } else if (this.bodyRotation == 6) {
            square.X--;
        } else if (this.bodyRotation == 7) {
            square.X--;
            square.Y--;
        }

        return square;
    }

    /**
     * Gets the square behind
     *
     * @return the square behind
     */
    public Position getSquareBehind() {
        Position square = this.copy();
        
        if (this.bodyRotation == 0) {
            square.Y++;
        } else if (this.bodyRotation == 1) {
            square.X--;
            square.Y++;
        } else if (this.bodyRotation == 2) {
            square.X--;
        } else if (this.bodyRotation == 3) {
            square.X--;
            square.Y--;
        }  else if (this.bodyRotation == 4) {
            square.Y--;
        } else if (this.bodyRotation == 5) {
            square.X++;
            square.Y--;
        } else if (this.bodyRotation == 6) {
            square.X++;
        } else if (this.bodyRotation == 7) {
            square.X++;
            square.Y++;
        }

        return square;
    }

    /**
     * Gets the square to the right.
     *
     * @return the square to the right
     */
    public Position getSquareRight() {
        Position square = this.copy();

        if (this.bodyRotation == 0) {
            square.X++;
        } else if (this.bodyRotation == 1) {
            square.X++;
            square.Y++;
        } else if (this.bodyRotation == 2) {
            square.Y++;
        } else if (this.bodyRotation == 3) {
            square.X--;
            square.Y++;
        }  else if (this.bodyRotation == 4) {
            square.X--;
        } else if (this.bodyRotation == 5) {
            square.X--;
            square.Y--;
        } else if (this.bodyRotation == 6) {
            square.Y--;
        } else if (this.bodyRotation == 7) {
            square.X++;
            square.Y--;
        }

        return square;
    }

    /**
     * Gets the square to the right.
     *
     * @return the square to the right
     */
    public Position getSquareLeft() {
        Position square = this.copy();

        if (this.bodyRotation == 0) {
            square.X--;
        } else if (this.bodyRotation == 1) {
            square.X--;
            square.Y--;
        } else if (this.bodyRotation == 2) {
            square.Y--;
        } else if (this.bodyRotation == 3) {
            square.X++;
            square.Y--;
        }  else if (this.bodyRotation == 4) {
            square.X++;
        } else if (this.bodyRotation == 5) {
            square.X++;
            square.Y++;
        } else if (this.bodyRotation == 6) {
            square.Y++;
        } else if (this.bodyRotation == 7) {
            square.X--;
            square.Y++;
        }

        return square;
    }

    /**
     * Coords to create list of coordinates for a flat circle
     * @param radius the radius
     * @return the list of coordinates
     */
    public List<Position> getCircle(int radius) {
        List<Position> coords = new ArrayList<>();

        radius = (radius * 2); // Convert radius to diameter

        for (int x = this.getX() - radius; x <= this.getX() + radius; x++) {
            for (int y = this.getY() - radius; y <= this.getY() + radius; y++) {
                double dist = new Position(x, y).getDistanceSquared(this);

                if (dist <= radius) {
                    coords.add(new Position(x, y));
                }
            }
        }

        return coords;

        /*      for(int i = 0;i < circumference; i++)
        {
            double angle = i * increment;
            double x = this.getX() + (radius * Math.cos(angle));
            double y = this.getY() + (radius * Math.sin(angle));
            coords.add(new Position((int)x, (int)y, this.getZ()));
        }
        radius = (radius * 2); // Convert radius to diameter

        return coords;

        List<Position> coords = new ArrayList<>();

        for (int x = this.getX() - radius; x <= this.getX() + radius; x++) {
            for (int y = this.getY() - radius; y <= this.getY() + radius; y++) {
                double dist = new Position(x, y).getDistanceSquared(this);

                int cx = this.getX();
                int cy = (int)this.getZ();
                int cz = this.getY();

                boolean sphere = true;
                boolean hollow = false;
                int h = 1;

                for (int x = cx - radius; x <= cx + radius; x++) {
                    for (int z = cz - radius; z <= cz + radius; z++) {
                        //for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + h); y++) {
                        double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);// + (sphere ? (cy - y) * (cy - y) : 0);
                        if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))) {
                            //Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                            //circleblocks.add(l);
                            coords.add(new Position(x, z));
                        }
                        //}*/
    }
    
    /**
     * Copies the position.
     *
     * @return the position
     */
    public Position copy() {
        return new Position(this.X, this.Y, this.Z, this.headRotation, this.bodyRotation);
    }

    /**
     * Checks if is match, only checks X and Y coordinate, which is
     * intentional.
     *
     * @param obj the {@link Position}
     * @return true, if is match
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Position) {
            Position position = (Position) obj;
            return position.getX() == this.X && position.getY() == this.Y;
        }
        
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + this.X + ", " + this.Y + "]";
    }

}
