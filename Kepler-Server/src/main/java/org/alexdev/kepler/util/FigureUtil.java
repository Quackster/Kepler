package org.alexdev.kepler.util;

public class FigureUtil {
    public static Object renderFor(String figure, int version) {
        if (!figure.contains("hd") && version < 21) {
            return figure;
        }

        return figure;
    }
}
