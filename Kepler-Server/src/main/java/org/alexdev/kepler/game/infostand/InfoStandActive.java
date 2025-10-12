package org.alexdev.kepler.game.infostand;

public class InfoStandActive {

    private int plate;
    private int furni;
    private String expression;
    private String action;
    private int direction;

    public InfoStandActive() {
        this.plate = InfoStand.DEFAULT_PLATE;
        this.furni = InfoStand.DEFAULT_FURNI;
        this.expression = InfoStand.DEFAULT_EXPRESSION;
        this.action = InfoStand.DEFAULT_ACTION;
        this.direction = InfoStand.DEFAULT_DIRECTION;
    }

    public void fill(int plate, int furni, String expression, String action, int direction) {
        this.plate = plate;
        this.furni = furni;
        this.expression = expression;
        this.action = action;
        this.direction = direction;
    }

    public int getPlate() {
        return plate;
    }

    public void setPlate(int plate) {
        this.plate = plate;
    }

    public int getFurni() {
        return furni;
    }

    public void setFurni(int furni) {
        this.furni = furni;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

}
