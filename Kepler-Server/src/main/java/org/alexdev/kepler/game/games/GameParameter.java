package org.alexdev.kepler.game.games;

public class GameParameter {
    private String name;
    private boolean editable;
    private boolean hasMinMax;
    private int min;
    private int max;
    private String defaultValue;

    public GameParameter(String name, boolean editable, String defaultValue, int min, int max) {
        this.name = name;
        this.editable = editable;
        this.defaultValue = defaultValue;
        this.hasMinMax = true;
        this.min = min;
        this.max = max;
    }

    public GameParameter(String name, boolean editable, String defaultValue) {
        this.name = name;
        this.editable = editable;
        this.defaultValue = defaultValue;
        this.hasMinMax = false;
    }

    public String getName() {
        return name;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean hasMinMax() {
        return hasMinMax;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}