package org.alexdev.roseau.game.room.model;

public class Position {

    private int X;
    private int Y;
    private double Z;
    private int bodyRotation;
    private int headRotation;

    public Position() {
        this(0, 0, 0);

    }

    public Position(String position) {
        String[] coords = position.split(",", 2);
        this.X = Short.parseShort(coords[0]);
        this.Y = Short.parseShort(coords[1]);
        this.bodyRotation = 0;
        this.headRotation = 0;
        this.Z = 0;
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

    public Position(int x, int y, double z, int rotation) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.bodyRotation = rotation;
        this.headRotation = rotation;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double z) {
        Z = z;
    }

    public Position add(Position other) {
        return new Position(other.getX() + getX(), other.getY() + getY(), other.getZ() + getZ());
    }

    public Position subtract(Position other) {
        return new Position(other.getX() - getX(), other.getY() - getY(), other.getZ() - getZ());
    }


    public int getDistanceSquared(Position point) {
        int dx = this.getX() - point.getX();
        int dy = this.getY() - point.getY();

        return (dx * dx) + (dy * dy);
    }

    public int getDistance(Position point) {
        return (int)Math.hypot(this.getX() - point.getX(), this.getY() - point.getY());
    }


    public boolean isMatch(Position point) {	
        return (this.X == point.getX() && this.Y == point.getY());
    }

    public int getRotation() {
        return bodyRotation;
    }

    public void setRotation(int rotation) {
        this.bodyRotation = rotation;
        this.headRotation = rotation;
    }

    public void setBodyRotation(int rotation) {
        this.bodyRotation = rotation;
    }

    public int getHeadRotation() {
        return headRotation;
    }

    public void setHeadRotation(int headRotation) {
        this.headRotation = headRotation;
    }

    public Position getSquareInFront() {
        Position square = new Position(this.X, this.Y);

        if (this.bodyRotation == 0) {
            square.Y--;
        } else if (this.bodyRotation == 2) {
            square.X++;
        } else if (this.bodyRotation == 4) {
            square.Y++;
        } else if (this.bodyRotation == 6) {
            square.X--;
        }

        return square;
    }

    public Position getSquareLeft() {

        Position square = new Position(this.X, this.Y);

        if (this.bodyRotation == 0) {
            square.X--;
        } else if (this.bodyRotation == 2) {
            square.Y++;
        } else if (this.bodyRotation == 4) {
            square.X++;
        } else if (this.bodyRotation == 6) {
            square.Y++;
        }

        return square;
    }
    
    public Position getSquareRight() {

        Position square = new Position(this.X, this.Y);

        if (this.bodyRotation == 0) {
            square.X++;
        } else if (this.bodyRotation == 2) {
            square.Y--;
        } else if (this.bodyRotation == 4) {
            square.X--;
        } else if (this.bodyRotation == 6) {
            square.Y--;
        }

        return square;
    }

    public void set(Position position) {
        this.X = position.getX();
        this.Y = position.getY();
        this.Z = position.getZ();
        this.bodyRotation = position.getRotation();
        this.headRotation = position.getRotation();
    }

    @Override
    public String toString() {

        return this.X + "," + this.Y;
    }


}
