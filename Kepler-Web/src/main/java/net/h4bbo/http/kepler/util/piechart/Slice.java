package net.h4bbo.http.kepler.util.piechart;

import java.awt.*;

public class Slice {

    private String label;
    private double value;
    private Color color;

    public Slice(String label, double value, Color color) {
        this.label = label;
        this.value = value;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public double getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }
}





