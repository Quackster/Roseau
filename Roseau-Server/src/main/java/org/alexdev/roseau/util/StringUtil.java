package org.alexdev.roseau.util;

public class StringUtil {
    /**
     * Round to two decimal places.
     *
     * @param decimal the decimal
     * @return the double
     */
    public static double format(double decimal) {
        return Math.round(decimal * 100.0) / 100.0;
    }
}
